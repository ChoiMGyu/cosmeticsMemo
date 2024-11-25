package com.example.groupProject.controller.validator;

import com.example.groupProject.dto.memo.SkincareDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MemoApiValidator {
    private static final String END_DATE_BEFORE_NOW = "사용 기한 마지막 날짜는 현재 날짜 이전으로 설정할 수 없습니다.";
    private static final String DATE_COMPARE_MESSAGE = "사용 기한 마지막 날짜가 개봉 날짜 이전으로 설정할 수 없습니다.";

    public void validateDate(SkincareDto skincareDto) {
        if(skincareDto.getEnd_date() != null && skincareDto.getStart_date() == null) {
            if(skincareDto.getEnd_date().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException(END_DATE_BEFORE_NOW);
            }
        }
        if (skincareDto.getStart_date() != null && skincareDto.getEnd_date() != null) {
            if (skincareDto.getStart_date().isAfter(skincareDto.getEnd_date())) {
                throw new IllegalArgumentException(DATE_COMPARE_MESSAGE);
            }
        }
    }
}
