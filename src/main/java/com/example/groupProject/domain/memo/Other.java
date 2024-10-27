package com.example.groupProject.domain.memo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@DiscriminatorValue("O")
@Getter
public class Other extends Memo{

    private String productType; //제품의 유형 ex) 향수, 바디로션 등

    private String ingredients; //기타 특별한 성분 ex) 알레르기 유발 성분
}
