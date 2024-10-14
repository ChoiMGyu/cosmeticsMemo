package com.example.groupProject.controller.memo;

import com.example.groupProject.controller.user.UserApiController;
import com.example.groupProject.domain.Memo.Skincare;
import com.example.groupProject.domain.User.User;
import com.example.groupProject.dto.jwt.CustomUserDetails;
import com.example.groupProject.dto.memo.SkincareDto;
import com.example.groupProject.service.UserServiceImpl;
import com.example.groupProject.service.memo.SkincareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memo")
public class MemoApiController {

    private static final Logger logger = LoggerFactory.getLogger(MemoApiController.class);

    private final SkincareService skincareService;

    private final UserServiceImpl userService;

    @PostMapping("/createSkincare")
    public ResponseEntity<String> createSkincareMemo(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody SkincareDto skincareDto) {
        logger.info("MemoApiController - Skincare에 관련된 메모를 저장");

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        List<User> user = userService.findByAccount(customUserDetails.getUsername());

        LocalDate startDate = skincareDto.getStart_date() != null ? skincareDto.getStart_date() : LocalDate.now();
        LocalDate endDate = skincareDto.getEnd_date() != null ? skincareDto.getEnd_date() : startDate.plusMonths(6);

        Skincare skincare = Skincare.builder()
                .start_date(startDate)
                .end_date(endDate)
                .name(skincareDto.getName())
                .description(skincareDto.getDescription())
                .master(user.get(0))
                .area(skincareDto.getArea())
                .moisture(skincareDto.getMoisture())
                .build();

        skincareService.saveSkincareMemo(skincare);

        return ResponseEntity.status(HttpStatus.OK).body("스킨케어 메모가 저장되었습니다.");
    }
}
