package com.example.groupProject.service;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.service.memo.SkincareService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class MemoServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private SkincareService skincareService;

    private User user;
    private Skincare skincare;

    @BeforeEach
    public void setUp() {
        user = User.createUser("account", "password", null, null, null, null, null);
        userService.join(user);

        skincare = Skincare.builder()
                .start_date(LocalDate.now())
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .master(user)
                .area("얼굴")
                .build();

        skincareService.saveSkincareMemo(skincare);
        em.flush();
    }

    @Test
    @DisplayName("스킨케어 메모를 올바르게 저장한다")
    public void 메모저장() throws Exception {
        //given
        Skincare skincareTest = Skincare.builder()
                .start_date(LocalDate.now())
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .master(user)
                .area("얼굴")
                .build();

        //end_date와 moisture을 제외한 나머지 필드 설정

        //when
        Long memoId = skincareService.saveSkincareMemo(skincareTest);

        //then
        Skincare findMemo = skincareService.findById(memoId);
        //@Transactional에 의해 플러쉬되어도 영속성 컨텍스트는 비우지 않음
        //select 쿼리를 확인하고자 savaSkincareMemo 메서드에서 em.clear()를 하여 강제로 영속성 컨텍스트를 비움

        Assertions.assertEquals(findMemo, skincareTest);
        //메모리 주소가 아닌 객체의 내용을 비교
    }

    @Test
    @DisplayName("스킨케어 메모를 올바르게 삭제한다")
    public void 메모삭제() throws Exception {
        //given

        //when
        skincareService.deleteSkincareMemo(skincare);
        em.flush(); //delete 쿼리 확인용

        //then
        Skincare deletedSkincare = skincareService.findById(skincare.getId());
        Assertions.assertNull(deletedSkincare);
    }

    @Test
    @DisplayName("기한이 지난 날짜의 메모를 반환한다")
    public void 기한이_지난메모_반환() throws Exception {
        //given
        Skincare skincareTest = Skincare.builder()
                .start_date(LocalDate.now().minusDays(2))
                .end_date(LocalDate.now().minusDays(1))
                .name("테스트 화장품 1")
                .description("기한이 지난 화장품입니다.")
                .master(user)
                .area("얼굴")
                .build();

        skincareService.saveSkincareMemo(skincareTest);

        //when
        //memoService.checkExpiredMemos();

        //then
    }

    @Test
    @DisplayName("사용기한 마지막 날짜가 시작 날짜 이전으로 설정할 수 없다")
    public void 사용기한_설정() throws Exception {
        //given

        //when

        //then
    }


}