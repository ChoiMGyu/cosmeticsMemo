package com.example.groupProject.controller.validator;

import com.example.groupProject.dto.memo.SkincareDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class MemoValidatorTest {

    private MemoApiValidator memoApiValidator;

    @BeforeEach
    public void setUp() {
        memoApiValidator = new MemoApiValidator();
    }

    @Test
    @DisplayName("시작 날짜와 마지막 날짜를 올바르게 입력한다")
    public void 날짜입력_성공() throws Exception
    {
        //given
        SkincareDto skincareDto = SkincareDto.builder()
                .start_date(LocalDate.now())
                .end_date(LocalDate.now().plusDays(10))
                .build();

        //when

        //then
        assertDoesNotThrow(() -> memoApiValidator.validateDate(skincareDto));
    }

    @Test
    @DisplayName("시작 날짜를 입력하지 않았을 때 마지막 날짜가 과거일 수 없다")
    public void 시작날짜X_마지막날짜_현재이후() throws Exception
    {
        //given
        SkincareDto skincareDto = SkincareDto.builder()
                .end_date(LocalDate.now().minusDays(1))
                .build();

        //when

        //then
        assertThatThrownBy(() -> memoApiValidator.validateDate(skincareDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("시작 날짜와 마지막 날짜가 모두 입력되었을 때 마지막 날짜는 시작 날짜 이전일 수 없다")
    public void 시작날짜_마지막날짜_관계() throws Exception
    {
        //given
        SkincareDto skincareDto = SkincareDto.builder()
                .start_date(LocalDate.now().plusDays(10))
                .end_date(LocalDate.now())
                .build();

        //when

        //then
        assertThatThrownBy(() -> memoApiValidator.validateDate(skincareDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
