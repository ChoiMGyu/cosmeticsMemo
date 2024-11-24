package com.example.groupProject.service.memo;

import com.example.groupProject.domain.memo.Memo;
import com.example.groupProject.domain.user.DeviceToken;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.memo.FcmMessageDto;
import com.example.groupProject.dto.memo.FcmSendDeviceDto;
import com.example.groupProject.dto.memo.FcmSendDto;
import com.example.groupProject.repository.memo.FcmRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FcmServiceImpl implements FcmService {

    @Value("${json.file.path}")
    private String firebaseConfigPath;

    private final FcmRepository fcmRepository;

    @Override
    @Transactional
    public Long saveDeviceToken(String deviceToken, User user) {
        DeviceToken userDeviceToken = new DeviceToken(deviceToken, user);
        fcmRepository.save(userDeviceToken);
        return userDeviceToken.getId();
    }

    @Override
    public void sendMessageTo(FcmSendDto fcmSendDto) throws IOException {
        //메시지를 구성하고 토큰을 받아서 FCM으로 메시지 처리를 수행하는 비즈니스 로직
        String message = makeMessage(fcmSendDto);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(message, headers);

        String API_URL = "https://fcm.googleapis.com/v1/projects/groupproject-dceb5/messages:send";

        try {
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            log.info("FCM 전송 완료 상태 코드 : " + response.getStatusCode());
        } catch (Exception e) {
            log.error("[-] FCM 전송 오류 :: " + e.getMessage());
            log.error("[-] 오류 발생 토큰 :: [" + fcmSendDto.getToken() + "]");
            log.error("[-] 오류 발생 메시지 :: [" + fcmSendDto.getBody() + "]");
        }
    }

    @Override
    public List<FcmSendDeviceDto> selectFcmSendList() {
        //1. 알림을 전송 해야할 메모를 찾는다
        //2. 메모들의 작성자를 찾는다
        //3. 작성자의 디바이스 토큰을 반환한다

        List<Memo> expiredMemo = fcmRepository.findByEndDateAfter();

        List<FcmSendDeviceDto> fcmSendDeviceDtos = new ArrayList<>();
        for (Memo memo : expiredMemo) {
            Long userId = memo.getMaster().getId();
            List<String> deviceTokens = fcmRepository.findDeviceTokensByMemo(userId);
            for (String token : deviceTokens) {
                fcmSendDeviceDtos.add(new FcmSendDeviceDto(token, memo.getName(), memo.getMaster().getAccount(), memo.getEnd_date()));
            }
        }

        return fcmSendDeviceDtos;
    }

    private String getAccessToken() throws IOException {
        //Firebase Admin SDK의 비공개 키를 참조하여 Bearer 토큰을 발급 받습니다

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeMessage(FcmSendDto fcmSendDto) throws JsonProcessingException {
        //FCM 전송 정보를 기반으로 메시지를 구성합니다 (Object -> String)
        ObjectMapper om = new ObjectMapper();
        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(fcmSendDto.getToken())
                        .notification(FcmMessageDto.Notification.builder()
                                .title(fcmSendDto.getTitle())
                                .body(fcmSendDto.getBody())
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return om.writeValueAsString(fcmMessageDto);
    }
}
