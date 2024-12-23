package com.example.groupProject.domain.board;

import com.example.groupProject.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board extends Timestamped {
    private static final int INITIAL_HIT = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title; //게시물 제목

    private String content; //게시물 내용

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
