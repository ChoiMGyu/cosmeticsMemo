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
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeMessage(FcmSendDto fcmSendDto) throws JsonProcessingException {
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
