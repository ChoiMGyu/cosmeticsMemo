package com.example.groupProject.domain.memo;

import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.memo.SkincareDto;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("S")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
public class Skincare extends Memo {

    private String area; //사용 부위 ex) 얼굴 전체, 눈가, 입가

    private String moisture; //보습 정도 ex) 겨울에 발랐더니 건조했다

    @Builder
    public Skincare(LocalDate start_date, LocalDate end_date, String name, String description, User master, String area, String moisture) {
        super(start_date, end_date, name, description, master);
        initialArea(area);
        initialMoisture(moisture);
    }

    private void initialArea(String area) {
        this.area = area;
    }

    private void initialMoisture(String moisture) {
        this.moisture = moisture;
    }

    public void changeSkincare(SkincareDto skincareDto) {
        changeMemo(skincareDto.getEnd_date(), skincareDto.getStart_date(), skincareDto.getName(), skincareDto.getDescription());
        this.area = skincareDto.getArea();
        this.moisture = skincareDto.getMoisture();
    }
}
