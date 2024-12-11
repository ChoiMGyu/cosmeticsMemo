package com.example.groupProject.controller.memo;

import com.example.groupProject.controller.message.ErrorMessage;
import com.example.groupProject.controller.validator.MemoApiValidator;
import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.jwt.CustomUserDetails;
import com.example.groupProject.dto.memo.SkincareAllDto;
import com.example.groupProject.dto.memo.SkincareDto;
import com.example.groupProject.service.UserServiceImpl;
import com.example.groupProject.service.memo.SkincareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memo")
public class MemoApiController {
    private static final int MEMO_WRITER = 0;
    private static final String SUCCESS_FINDALL_SKINCARE_MEMO_MESSAGE = "스킨케어 메모를 모두 찾아왔습니다.";
    private static final String SUCCESS_CREATE_SKINCARE_MEMO_MESSAGE = "스킨케어 메모가 저장되었습니다.";
    private static final String SUCCESS_DELETE_SKINCARE_MEMO_MESSAGE = "스킨케어 메모가 삭제되었습니다.";
    private static final String SUCCESS_UPDATE_SKINCARE_MEMO_MESSAGE = "스킨케어 메모가 수정되었습니다.";

    private static final Logger logger = LoggerFactory.getLogger(MemoApiController.class);

    private final SkincareService skincareService;
    private final UserServiceImpl userService;
    private final MemoApiValidator memoApiValidator;

    @GetMapping("/skincare")
    public ResponseEntity<SkincareAllDto> findAllSkincareMemo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                              @RequestParam(value = "page", defaultValue = "0") int page,
                                                              @RequestParam(value = "size", defaultValue = "5") int size,
                                                              @RequestParam(value = "sortBy", defaultValue = "start_date") String sortBy) {
        logger.info("MemoApiController - Skincare에 관련된 메모를 찾기");

        if(customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(SkincareAllDto.of(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage(), null, null));
        }

        List<User> user = userService.findByAccount(customUserDetails.getUsername());

        Page<SkincareDto> skincareMemoPage = skincareService.findAllSkincareMemoPagingByUserId(user.get(MEMO_WRITER).getId(), page, size, sortBy);

        return ResponseEntity.status(HttpStatus.OK).body(SkincareAllDto.of(SUCCESS_FINDALL_SKINCARE_MEMO_MESSAGE, customUserDetails.getUsername(), skincareMemoPage.getContent()));
    }

    @PostMapping("/skincare")
    public ResponseEntity<String> createSkincareMemo(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody SkincareDto skincareDto) {
        logger.info("MemoApiController - Skincare에 관련된 메모를 저장");

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        List<User> user = userService.findByAccount(customUserDetails.getUsername());

        memoApiValidator.validateDate(skincareDto);

        skincareService.saveSkincareMemo(skincareDto, user.get(MEMO_WRITER));

        logger.info("MemoApiController - 메모가 정상적으로 저장되었습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_CREATE_SKINCARE_MEMO_MESSAGE);
    }

    @DeleteMapping("/skincare")
    public ResponseEntity<String> deleteSkincareMemo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @RequestParam(value = "id") Long id) {
        logger.info("MemoApiController - Skincare에 관련된 메모를 삭제");

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        skincareService.deleteByIdSkincareMemo(id);

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_DELETE_SKINCARE_MEMO_MESSAGE);
    }

    @PutMapping("/skincare")
    public ResponseEntity<String> updateSkincareMemo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @RequestParam(value = "id") Long id,
                                                     @RequestBody @Valid SkincareDto skincareDto) {
        logger.info("MemoApiController - Skincare에 관련된 메모를 수정");

        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorMessage.LOGIN_REQUIRED_MESSAGE.getMessage());
        }

        memoApiValidator.validateDate(skincareDto);

        skincareService.updateSkincareMemo(id, skincareDto);

        return ResponseEntity.status(HttpStatus.OK).body(SUCCESS_UPDATE_SKINCARE_MEMO_MESSAGE);
    }

}
