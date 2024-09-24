package com.example.groupProject.domain.Memo;

import com.example.groupProject.domain.Memo.Memo;
import com.example.groupProject.domain.SkinType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Entity
@DiscriminatorValue("S")
@Getter
public class Skincare extends Memo {

    private String area; //사용 부위 ex) 얼굴 전체, 눈가, 입가

    private String moisture; //보습 정도 ex) 겨울에 발랐더니 건조했다
}
