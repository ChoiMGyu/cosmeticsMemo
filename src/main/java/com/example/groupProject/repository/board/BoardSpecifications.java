package com.example.groupProject.repository.board;

import com.example.groupProject.domain.board.Board;
import org.springframework.data.jpa.domain.Specification;

public class BoardSpecifications {

    public static Specification<Board> withMasterId(Long id) {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("id")));
            return criteriaBuilder.equal(root.get("master").get("id"), id);
        };
    }

    public static Specification<Board> sortBy(String sortBy) {
        return (root, query, criteriaBuilder) -> {
            if (sortBy != null) {
                switch (sortBy) {
                    case "register":
                        query.orderBy(criteriaBuilder.desc(root.get("register")));
                        break;
                    case "hit":
                        query.orderBy(criteriaBuilder.desc(root.get("hit")));
                        break;
                    case "like":
                        query.orderBy(criteriaBuilder.asc(root.get("like")));
                        break;
                    default:
                        query.orderBy(criteriaBuilder.asc(root.get("id")));
                }
            }
            return null;
        };
    }
}
