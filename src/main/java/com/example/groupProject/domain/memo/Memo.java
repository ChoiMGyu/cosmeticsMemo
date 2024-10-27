package com.example.groupProject.domain.memo;

import com.example.groupProject.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public abstract class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;

    private LocalDate start_date = LocalDate.now(); //화장품 개봉 일자

    private LocalDate end_date = start_date.plusMonths(6); //화장품 소비 기한 (기본값은 6개월 이후)

    private String name; //화장품 이름

    private String description; //부가 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User master;

    protected Memo(LocalDate start_date, LocalDate end_date, String name, String description, User master) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.name = name;
        this.description = description;
        this.master = master;
    }
}
