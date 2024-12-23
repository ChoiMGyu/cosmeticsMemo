package com.example.groupProject.service_unit;

import com.example.groupProject.repository.board.LikesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("tests")
public class LikesServiceTest {

    @Mock
    private LikesRepository likesRepository;

    @Test
    @DisplayName("좋아요를 눌렀을 때 숫자가 증가한다")
    public void 좋아요수_증가() throws Exception
    {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("좋아요를 다시 눌렀을 때 숫자가 감소한다")
    public void 좋아요수_감소() throws Exception
    {
        //given

        //when

        //then
    }
    
    @Test
    @DisplayName("좋아요를 누른 사용자가 저장된다")        
    public void 좋아요_사용자_저장() throws Exception 
    {
        //given
        
        //when
        
        //then
    }
        

    @Test
    @DisplayName("좋아요를 누른 사람을 찾을 수 있다")
    public void 좋아요_주인() throws Exception
    {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("게시글의 총 좋아요 수를 확인할 수 있다")
    public void 총_좋아요수() throws Exception
    {
        //given

        //when

        //then
    }

    @Test
    @DisplayName("Redis에서 게시글에 대한 좋아요 수에 대한 데이터를 초기화한다")        
    public void 좋아요_데이터_초기화() throws Exception 
    {
        //given
        
        //when
        
        //then
    }
        

}
