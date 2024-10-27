package com.example.groupProject.controller;

import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.RoleType;
import com.example.groupProject.domain.user.SkinType;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc //MockMVC 객체를 빈으로 등록하지 않기 때문에 필요, 프로젝트에 있는 스프링 빈을 모두 등록
public class LoginTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {

        User user = User.createUser("account_test", "password", LocalDate.now(), SkinType.DRY, true, true, RoleType.ROLE_USER);

        userService.join(user);
    }

    @Test
    @WithUserDetails(value = "account_test", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void 로그인사용자_서비스접근() throws Exception
    {
        //given
        User user = userService.findByAccount("account_test").get(0);

        Skincare skincare = Skincare.builder()
                .start_date(LocalDate.now())
                .end_date(LocalDate.now().plusMonths(6))
                .name("토리든")
                .description("알로에 첨가")
                .master(user)
                .area("얼굴")
                .moisture("끈적")
                .build();

        //when

        //then
        mockMvc.perform(post("/api/memo/createSkincare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skincare)))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void 로그인X사용자_서비스접근() throws Exception
    {
        //given
        User user = userService.findByAccount("account_test").get(0);

        Skincare skincare = Skincare.builder()
                .start_date(LocalDate.now())
                .end_date(LocalDate.now().plusMonths(6))
                .name("토리든")
                .description("알로에 첨가")
                .master(user)
                .area("얼굴")
                .moisture("끈적")
                .build();

        //when

        //then
        mockMvc.perform(post("/api/memo/createSkincare")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skincare)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void withAnonymousUser_테스트_1() throws Exception
    {
        //given

        //when
        MvcResult mvcResult = mockMvc.perform(get("/test/not"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("not anonymous", content);
    }

    @Test
    @WithAnonymousUser
    public void withAnonymousUser_테스트_2() throws Exception
    {
        //given

        //when
        MvcResult mvcResult = mockMvc.perform(get("/test/user"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertEquals("anonymous", content);
    }

}
