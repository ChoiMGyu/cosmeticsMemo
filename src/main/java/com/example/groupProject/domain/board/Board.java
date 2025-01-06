package com.example.groupProject.domain.board;

import com.example.groupProject.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board extends Timestamped {
    private static final int INITIAL_HIT = 0;
    private static final int INITIAL_LIKE = 0;
    private static final String INVALID_LIKE_MESSAGE = "좋아요 개수는 0개 이하일 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title; //게시물 제목

    private String content; //게시물 내용

    @Builder.Default
    private Integer hit = INITIAL_HIT; //조회수

    @Builder.Default
    @Column(name = "like_count")
    private Integer like = INITIAL_LIKE; //좋아요 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User master;

    @Builder.Default
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<Comment> comments = new ArrayList<>();

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

    public void increment() {
        this.like++;
    }

    public void decrement() {
        if (this.like <= INITIAL_LIKE) {
            throw new IllegalStateException(INVALID_LIKE_MESSAGE);
        }
        this.like--;
    }

    public void changeLikeCount(int likeCount) {
        this.like = likeCount;
    }

    public boolean likeCountUpdateCompare(int likeCount) {
        if (this.like != likeCount) return true;
        else return false;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setBoard(this);
    }
}
