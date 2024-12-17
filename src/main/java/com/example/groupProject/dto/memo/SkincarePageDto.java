package com.example.groupProject.dto.memo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkincarePageDto {

    private Long masterId;

    private int page;

    private int size;

    private String sortBy;
}
