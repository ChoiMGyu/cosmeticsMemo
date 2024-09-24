package com.example.groupProject.domain.Memo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //하나의 테이블에서 모두 관리
@DiscriminatorColumn(name = "dtype")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;

    private LocalDate start_date = LocalDate.now(); //화장품 개봉 일자

    private LocalDate end_date = start_date.plusMonths(6); //화장품 소비 기한 (기본값은 6개월 이후)

    private String name; //화장품 이름

    private String description; //부가 설명
}
