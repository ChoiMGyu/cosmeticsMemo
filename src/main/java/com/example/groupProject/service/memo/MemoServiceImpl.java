//package com.example.groupProject.service.memo;
//
//import com.example.groupProject.domain.memo.Memo;
//import com.example.groupProject.repository.memo.FcmRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class MemoServiceImpl implements MemoService {
//
//    private final FcmRepository fcmRepository;
//
//    @Override
//    @Scheduled(cron = "0 0 10 * * ?", zone = "Asia/Seoul")
//    public void checkExpiredMemos() {
//        List<Memo> expriedMemo = fcmRepository.findByEndDateAfter();
//
//    }
//}
