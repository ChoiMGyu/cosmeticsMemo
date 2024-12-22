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
public class Board extends Timestamped {
    private static final int INITIAL_LIKE = 0;
    private static final int INITIAL_HIT = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title; //게시물 제목

    private String content; //게시물 내용

    @Builder.Default
    @Column(name = "like_count")
    private Integer like = INITIAL_LIKE; //좋아요 수

    @Builder.Default
    private Integer hit = INITIAL_HIT; //조회수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User master;

    public void changeBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public boolean isSameWriter(String writerAccount) {
        if (!master.getAccount().equals(writerAccount)) {
            return false;
        }
        return true;
    }
}
