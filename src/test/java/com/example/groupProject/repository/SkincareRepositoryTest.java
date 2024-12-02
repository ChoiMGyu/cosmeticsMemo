package com.example.groupProject.repository;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.repository.memo.SkincareRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class SkincareRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SkincareRepository skincareRepository;

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
        em.clear();

        //then
        Optional<Skincare> findSkincare = skincareRepository.findById(skincare.getId());
        assertTrue(findSkincare.isEmpty());
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
        Optional<Skincare> findSkincare = skincareRepository.findById(skincare.getId());
        assertTrue(findSkincare.isPresent());
        assertThat(findSkincare.get()).isEqualTo(skincare);
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
        Optional<Skincare> findSkincare = skincareRepository.findById(skincare.getId());

        //then
        assertTrue(findSkincare.isPresent());
        assertThat(findSkincare.get()).isEqualTo(skincare);
    }


}
