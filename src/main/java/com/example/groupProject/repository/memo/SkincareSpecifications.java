package com.example.groupProject.repository.memo;

import com.example.groupProject.domain.memo.Skincare;
import org.springframework.data.jpa.domain.Specification;

public class SkincareSpecifications {

    public static Specification<Skincare> withUserId(Long id) {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("start_date")));
            return criteriaBuilder.equal(root.get("master").get("id"), id);
        };
    }

    public static Specification<Skincare> withNotDeleted() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("deleted"), false);
        };
    }

    public static Specification<Skincare> sortBy(String sortBy) {
        return (root, query, criteriaBuilder) -> {
            if (sortBy != null) {
                switch (sortBy) {
                    case "end_date":
                        query.orderBy(criteriaBuilder.asc(root.get("end_date")));
                        break;
                    case "area":
                        query.orderBy(criteriaBuilder.asc(root.get("area")));
                        break;
                    case "moisture":
                        query.orderBy(criteriaBuilder.asc(root.get("moisture")));
                        break;
                    default:
                        query.orderBy(criteriaBuilder.asc(root.get("start_date")));
                }
            }
            return null;
        };
    }
}
