package com.example.groupProject.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class LikesServiceTests {

    @Test
    @DisplayName("좋아요 수를 일정 시간마다 DB에 업데이트한다")
    public void 좋아요수_DB_업데이트() throws Exception
    {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("좋아요를 눌렀을 때 누른 사람을 저장한다")
    public void 좋아요_누른사람() throws Exception
    {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("여러 사용자가 동시에 좋아요를 눌렀을 때 올바르게 동작한다")
    public void 좋아요_동시성() throws Exception
    {
        //given

        //when

        //then
    }

}
