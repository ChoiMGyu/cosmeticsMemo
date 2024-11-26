package com.example.groupProject.service;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.memo.FcmSendDeviceDto;
import com.example.groupProject.dto.memo.SkincareDto;
import com.example.groupProject.service.memo.FcmService;
import com.example.groupProject.service.memo.SkincareService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class FcmServiceTest {

    @Value("${fcm.device-token}")
    private String deviceToken;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private SkincareService skincareService;

    @Autowired
    private FcmService fcmService;

    @BeforeEach
    public void setUp() {
        User user1 = User.createUser("account", "password", null, null, null, null, null);
        userService.join(user1);

        User user2 = User.createUser("account2", "password2", null, null, null, null, null);
        userService.join(user2);

        User user3 = User.createUser("account3", "password3", null, null, null, null, null);
        userService.join(user3);

        Skincare skincare = Skincare.builder()
                .start_date(LocalDate.now().minusDays(3))
                .end_date(LocalDate.now().minusDays(2))
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품 입니다.")
                .master(user1)
                .area("얼굴")
                .build();

        Skincare totalcare = Skincare.builder()
                .start_date(LocalDate.now().minusDays(3))
                .end_date(LocalDate.now().minusDays(2))
                .name("토탈케어 화장품")
                .description("한 번에 해결 가능한 화장품 입니다.")
                .master(user2)
                .area("얼굴")
                .build();

        Skincare samplecare = Skincare.builder()
                .start_date(LocalDate.now())
                .name("샘플케어 화장품")
                .description("샘플로 케어하는 날 사용하는 화장품 입니다.")
                .master(user3)
                .area("얼굴")
                .build();

        SkincareDto skincareDto = SkincareDto.from(skincare);
        SkincareDto totalcareDto = SkincareDto.from(totalcare);
        SkincareDto samplecareDto = SkincareDto.from(samplecare);

        skincareService.saveSkincareMemo(skincareDto, user1);
        skincareService.saveSkincareMemo(totalcareDto, user2);
        skincareService.saveSkincareMemo(samplecareDto, user3);

        fcmService.saveDeviceToken(deviceToken, user1);
        fcmService.saveDeviceToken(deviceToken, user2);
        fcmService.saveDeviceToken(deviceToken, user3);

        em.flush();
    }

    @Test
    @DisplayName("Web Push를 전송해야 할 메모를 찾고 작성자를 알아낸다")
    public void WebPush_전송_리스트() throws Exception {
        //given

        //when
        List<FcmSendDeviceDto> fcmSendDeviceDtos = fcmService.selectFcmSendList();

        //then
        assertEquals(fcmSendDeviceDtos.size(), 2);
        assertEquals(fcmSendDeviceDtos.get(0).cosmeticName(), "기초케어 화장품");
        assertEquals(fcmSendDeviceDtos.get(1).cosmeticName(), "토탈케어 화장품");
    }

    @Test
    @DisplayName("사용자에게 Web Push를 전송한다 (sendMessageTo())")
    public void WebPush_전송() throws Exception {
        //given

        //when

        //then
    }

}
