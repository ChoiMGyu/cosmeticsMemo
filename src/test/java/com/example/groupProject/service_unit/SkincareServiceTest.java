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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

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

}
