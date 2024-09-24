package com.example.groupProject.domain.Memo;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@DiscriminatorValue("C")
@Getter
public class Color extends Memo{

    private int shade_number; //컬러 옵션 ex) 21호, 23호 등

    private String color; //색조 화장품의 색상 명
}
