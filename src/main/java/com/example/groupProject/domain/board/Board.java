package com.example.groupProject.domain.board;

import com.example.groupProject.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board {
    private static final int INITIAL_LIKE = 0;
    private static final int INITIAL_HIT = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title; //게시물 제목

    private String content; //게시물 내용

    @Builder.Default
    private Integer like = INITIAL_LIKE; //좋아요 수

    private LocalDate register; //게시물 등록 일자

    @Builder.Default
    private Integer hit = INITIAL_HIT; //조회수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User master;
}
