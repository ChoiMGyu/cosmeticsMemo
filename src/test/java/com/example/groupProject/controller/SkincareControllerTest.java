package com.example.groupProject.controller;

import com.example.groupProject.annotation.WithMockCustomUser;
import com.example.groupProject.controller.memo.MemoApiController;
import com.example.groupProject.controller.validator.MemoApiValidator;
import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.memo.SkincareDto;
import com.example.groupProject.service.UserServiceImpl;
import com.example.groupProject.service.memo.SkincareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemoApiController.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class SkincareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private SkincareService skincareService;

    @MockBean
    private MemoApiValidator memoApiValidator;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Skincare skincare;

    @BeforeEach
    public void setUp() {
        user = User.createUser("account_test", "password", null, null, null, null, null);
        userService.join(user);

        skincare = spy(Skincare.builder()
                .start_date(LocalDate.now())
                .name("기초케어 화장품")
                .description("세안 후 첫 단계에 사용하는 화장품입니다.")
                .master(user)
                .area("얼굴")
                .moisture("촉촉함")
                .build());

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    @DisplayName("스킨케어 메모를 올바르게 저장한다")
    @WithMockCustomUser
    public void 메모저장() throws Exception {
        //given
        SkincareDto skincareDto = SkincareDto.from(skincare);
        when(skincareService.saveSkincareMemo(any(SkincareDto.class), any(User.class)))
                .thenReturn(1L);
        when(userService.findByAccount(Mockito.anyString()))
                .thenReturn(List.of(user));

        //when
        String content = objectMapper.writeValueAsString(skincareDto);

        //then
        mockMvc.perform(post("/api/memo/skincare")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @DisplayName("스킨케어 메모를 페이징하여 반환한다")
    @WithMockCustomUser
    @CsvSource({
            "start_date",
            "end_date",
            "area",
            "moisture"
    })
    public void 페이징_반환(String sortBy) throws Exception {
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

        SkincareDto skincareDto = SkincareDto.from(skincare);
        SkincareDto skincareDto1 = SkincareDto.from(skincare1);
        SkincareDto skincareDto2 = SkincareDto.from(skincare2);

        List<SkincareDto> skincareDtoMemos = List.of(skincareDto, skincareDto1, skincareDto2);
        Comparator<SkincareDto> comparator = switch (sortBy) {
            case "start_date" -> Comparator.comparing(SkincareDto::getStart_date);
            case "end_date" -> Comparator.comparing(SkincareDto::getEnd_date);
            case "area" -> Comparator.comparing(SkincareDto::getArea);
            case "moisture" -> Comparator.comparing(SkincareDto::getMoisture);
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
        };

        List<SkincareDto> sortedSkincareDtoMemos = skincareDtoMemos.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        Page<SkincareDto> skincarePage = new PageImpl<>(sortedSkincareDtoMemos.subList(0, 2));

        when(userService.findByAccount(Mockito.anyString()))
                .thenReturn(List.of(user));
        when(skincareService.findAllSkincareMemoPagingByUserId(eq(user.getId()), eq(0), eq(2), eq(sortBy)))
                .thenReturn(skincarePage);

        //when

        //then
        mockMvc.perform(get("/api/memo/skincare")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sortBy", sortBy)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

}
