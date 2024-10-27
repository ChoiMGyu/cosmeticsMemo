package com.example.groupProject.service;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.service.memo.SkincareService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class MemoServiceTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private SkincareService skincareService;

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void 상속관계_메모저장() throws Exception
    {
        //given
        User user = User.createUser("account", "password", null, null, null, null, null);
        userService.join(user);

        Skincare skincare = Skincare.builder()
                .start_date(LocalDate.now())
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .master(user)
                .area("얼굴")
                .build();

        //end_date와 moisture을 제외한 나머지 필드 설정

        //when
        Long memoId = skincareService.saveSkincareMemo(skincare);

        //then
        Skincare findMemo = skincareService.findById(memoId);
        //@Transactional에 의해 플러쉬되어도 영속성 컨텍스트는 비우지 않음
        //select 쿼리를 확인하고자 savaSkincareMemo 메서드에서 em.clear()를 하여 강제로 영속성 컨텍스트를 비움

        Assertions.assertEquals(findMemo, skincare);
        //메모리 주소가 아닌 객체의 내용을 비교
    }

}
