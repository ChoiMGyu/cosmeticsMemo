package com.example.groupProject.repository;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.repository.memo.SkincareRepository;
import com.example.groupProject.repository.memo.SkincareSpecifications;
import com.example.groupProject.repository.user.UserRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SkincareRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private SkincareRepository skincareRepository;


    @ParameterizedTest
    @DisplayName("회원의 스킨케어 메모를 정렬 기준별로 찾아온다")
    @CsvSource({
            "start_date, 기초케어 화장품 2, 기초케어 화장품",
            "end_date, 기초케어 화장품 2, 기초케어 화장품",
            "area, 기초케어 화장품 2, 기초케어 화장품 1",
            "moisture, 기초케어 화장품 2, 기초케어 화장품 1",
    })
    public void 정렬기준_스킨케어_메모찾기(String sortBy, String expectedFirst, String expectedSecond) throws Exception {
        //given
        User testUser = User.createUser("account", "password", null, null, null, null, RoleType.ROLE_USER);
        userRepository.save(testUser);

        Skincare skincare = Skincare.builder()
                .start_date(LocalDate.now().plusDays(1))
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .master(testUser)
                .area("얼굴")
                .moisture("촉촉함")
                .build();

        Skincare skincare1 = Skincare.builder()
                .start_date(LocalDate.now().plusDays(2))
                .master(testUser)
                .name("기초케어 화장품 1")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .area("몸")
                .moisture("유분기 적음")
                .build();

        Skincare skincare2 = Skincare.builder()
                .start_date(LocalDate.now())
                .master(testUser)
                .name("기초케어 화장품 2")
                .description("세안 후 두 번째 단계에 사용하는 화장품입니다.")
                .area("머리")
                .moisture("유분기 많음")
                .build();

        skincareRepository.save(skincare);
        skincareRepository.save(skincare1);
        skincareRepository.save(skincare2);
        em.flush();
        em.clear();

        //when
        Pageable pageable = PageRequest.of(0, 2);
        Specification<Skincare> spec = Specification.where(SkincareSpecifications.withUserId(testUser.getId()))
                .and(SkincareSpecifications.sortBy(sortBy));

        Page<Skincare> skincarePage = skincareRepository.findAll(spec, pageable);
        List<Skincare> skincarePageContent = skincarePage.getContent();

        //then
        assertThat(skincarePage).isNotNull();
        assertThat(skincarePageContent.get(0).getName()).isEqualTo(expectedFirst);
        assertThat(skincarePageContent.get(1).getName()).isEqualTo(expectedSecond);
    }
}
