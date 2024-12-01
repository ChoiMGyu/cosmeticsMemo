package com.example.groupProject.repository;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.repository.memo.SkincareRepository;
import com.example.groupProject.repository.memo.SkincareRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class SkincareRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    private SkincareRepository skincareRepository;

    @BeforeEach
    public void setUp() {
        skincareRepository = new SkincareRepositoryImpl(em);
    }

    @Test
    @DisplayName("특정 스킨케어 메모를 ID로 삭제할 수 있다")
    public void ID_메모_삭제() throws Exception {
        //given
        Skincare skincare = Skincare.builder()
                .start_date(LocalDate.now())
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .area("얼굴")
                .build();

        skincareRepository.save(skincare);

        //when
        skincareRepository.deleteById(skincare.getId());
        em.flush();

        //then
        Skincare findSkincare = skincareRepository.findById(skincare.getId());
        assertNull(findSkincare);
    }

    @Test
    @DisplayName("존재하지 않는 스킨케어 메모를 삭제할 때 오류가 발생한다")
    public void 존재X_삭제_오류() throws Exception {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("스킨케어 메모를 저장할 수 있다")
    public void 스킨케어_메모_저장() throws Exception {
        //given
        Skincare skincare = Skincare.builder()
                .start_date(LocalDate.now())
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .area("얼굴")
                .build();

        //when
        skincareRepository.save(skincare);
        em.flush();
        em.clear();

        //then
        Skincare findSkincare = skincareRepository.findById(skincare.getId());
        assertNotNull(skincare);
        assertEquals(skincare, findSkincare);
    }

    @Test
    @DisplayName("스킨케어 메모를 ID로 찾아올 수 있다")
    public void 스킨케어_메모_ID_찾기() throws Exception {
        //given
        Skincare skincare = Skincare.builder()
                .start_date(LocalDate.now())
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .area("얼굴")
                .build();

        skincareRepository.save(skincare);
        em.flush();
        em.clear();

        //when
        Skincare findSkincare = skincareRepository.findById(skincare.getId());

        //then
        assertThat(skincare).isEqualTo(findSkincare);
    }


}
