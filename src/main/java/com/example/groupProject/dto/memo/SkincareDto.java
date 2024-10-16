package com.example.groupProject.dto.memo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkincareDto {

    private LocalDate start_date; // 개봉 일자 (선택 사항)
    private LocalDate end_date; // 사용 기한 (선택 사항)

    @NotBlank(message = "화장품 이름은 필수 입력 사항입니다.") // 화장품 이름은 필수
    private String name; // 필수 입력 사항 (화장품 이름)

    private String description; //부가 설명 (선택 사항)

    private String area; // 사용 부위 (선택 사항)
    private String moisture; // 보습 정도 (선택 사항)
}
