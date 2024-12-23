package com.example.groupProject.domain.board;

import com.example.groupProject.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Likes {
    private static final int INITIAL_LIKE = 0;
    private static final String INVALID_LIKE_MESSAGE = "좋아요 개수는 0개 이하일 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @Builder.Default
    @Column(name = "like_count")
    private Integer like = INITIAL_LIKE; //좋아요 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void increment() {
        this.like++;
    }

    public void decrement() {
        if(this.like <= INITIAL_LIKE) {
            throw new IllegalStateException(INVALID_LIKE_MESSAGE);
        }
        this.like--;
    }
}
