package com.example.groupProject.domain.memo;

import com.example.groupProject.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public abstract class Memo {
    private static final int INITIAL_END_DATE = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;

    private LocalDate start_date; //화장품 개봉 일자

    private LocalDate end_date; //화장품 소비 기한 (기본값은 6개월 이후)

    private String name; //화장품 이름

    private String description; //부가 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User master;

    protected Memo(LocalDate start_date, LocalDate end_date, String name, String description, User master) {
        this.start_date = start_date != null ? start_date : LocalDate.now();
        if(end_date == null) {
            if(start_date == null) {
                this.end_date = LocalDate.now().plusMonths(INITIAL_END_DATE);
            }
            else {
                this.end_date = start_date.plusMonths(INITIAL_END_DATE);
            }
        }
        else {
            this.end_date = end_date;
        }
        this.name = name;
        this.description = description;
        this.master = master;
    }


    protected void changeMemo(LocalDate end_date, LocalDate start_date, String name, String description) {
        this.end_date = end_date;
        this.start_date = start_date;
        this.name = name;
        this.description = description;
    }
}
