package com.example.groupProject.domain.board;

import com.example.groupProject.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends Timestamped {
    private static final String NOT_SAME_WRITER = "댓글 작성자가 일치하지 않습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User master;

    public void isSameWriter(String writer) {
        if (!writer.equals(master.getAccount())) {
            throw new IllegalArgumentException(NOT_SAME_WRITER);
        }
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
