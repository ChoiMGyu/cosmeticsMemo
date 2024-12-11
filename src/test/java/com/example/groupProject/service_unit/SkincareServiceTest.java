package com.example.groupProject.service_unit;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.memo.SkincareDto;
import com.example.groupProject.repository.memo.SkincareRepository;
import com.example.groupProject.service.memo.SkincareServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class SkincareServiceTest {

    @Mock
    private SkincareRepository skincareRepository;

    @InjectMocks
    SkincareServiceImpl skincareService;

    private User user;

    @Spy
    private Skincare skincare;

    @BeforeEach
    void setUp() {
        user = User.createUser("account", "password", null, null, null, null, null);

        skincare = spy(Skincare.builder()
                .start_date(LocalDate.now())
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .master(user)
                .area("얼굴")
                .moisture("촉촉함")
                .build());
    }

    @Test
    @DisplayName("스킨케어 메모를 올바르게 저장한다")
    public void 메모저장() {
        //given
        SkincareDto skincareDto = SkincareDto.from(skincare);

        //when
        Long memoId = skincareService.saveSkincareMemo(skincareDto, user);

        //then
        verify(skincareRepository, times(1)).save(any(Skincare.class));
    }

    @Test
    @DisplayName("스킨케어 메모를 올바르게 삭제한다")
    public void 스킨케어_삭제() throws Exception {
        //given
        Long skincareId = 1L;

        when(skincareRepository.existsById(skincareId)).thenReturn(true);
        doNothing().when(skincareRepository).deleteById(anyLong());

        //when
        skincareService.deleteByIdSkincareMemo(skincareId);

        //then
        verify(skincareRepository, times(1)).deleteById(skincareId);
    }

    @ParameterizedTest
    @DisplayName("스킨케어를 정렬 기준에 따라 페이징하여 반환한다")
    @CsvSource({
            "start_date, 기초케어 화장품, 기초케어 화장품 1",
            "end_date, 기초케어 화장품, 기초케어 화장품 1",
            "area, 기초케어 화장품 2, 기초케어 화장품 1",
            "moisture, 기초케어 화장품 2, 기초케어 화장품 1"
    })
    public void 정렬기준_스킨케어_페이징(String sortBy, String expectedFirst, String expectedSecond) throws Exception {
        //given
        Skincare skincare1 = Skincare.builder()
                .start_date(LocalDate.now().plusDays(1))
                .name("기초케어 화장품 1")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .area("몸")
                .moisture("유분기 적음")
                .build();

        Skincare skincare2 = Skincare.builder()
                .start_date(LocalDate.now().plusDays(2))
                .name("기초케어 화장품 2")
                .description("세안 후 두 번째 단계에 사용하는 화장품입니다.")
                .area("머리")
                .moisture("유분기 많음")
                .build();

        List<Skincare> skincareMemos = List.of(skincare, skincare1, skincare2);
        Pageable pageable = PageRequest.of(0, 2);

        when(skincareRepository.findAll(any(Specification.class), eq(pageable)))
                .thenAnswer(invocation -> {
                    Specification<Skincare> specification = invocation.getArgument(0);
                    Comparator<Skincare> comparator = switch (sortBy) {
                        case "start_date" -> Comparator.comparing(Skincare::getStart_date);
                        case "end_date" -> Comparator.comparing(Skincare::getEnd_date);
                        case "area" -> Comparator.comparing(Skincare::getArea);
                        case "moisture" -> Comparator.comparing(Skincare::getMoisture);
                        default -> throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
                    };

                    List<Skincare> sortedSkincareMemos = skincareMemos.stream()
                            .sorted(comparator)
                            .collect(Collectors.toList());

                    int start = (int) pageable.getOffset();
                    int end = Math.min((start + pageable.getPageSize()), sortedSkincareMemos.size());
                    return new PageImpl<>(sortedSkincareMemos.subList(start, end), pageable, skincareMemos.size());
                });


        //when
        Page<SkincareDto> resultPage = skincareService.findAllSkincareMemoPagingByUserId(1L, 0, 2, sortBy);


        //then
        assertNotNull(resultPage);
        assertThat(resultPage.getContent().size()).isEqualTo(2);
        assertThat(resultPage.getContent().get(0).getName()).isEqualTo(expectedFirst);
        assertThat(resultPage.getContent().get(1).getName()).isEqualTo(expectedSecond);
    }
}
