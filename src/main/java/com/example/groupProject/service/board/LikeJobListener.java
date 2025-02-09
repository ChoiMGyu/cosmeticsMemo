package com.example.groupProject.service.board;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

@Slf4j
public class LikeJobListener implements JobListener {
    @Override
    public String getName() {
        return "Like Write Scheduler";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.info("[-] Like Job이 실행 되기 전 수행됩니다");
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info("[-] Like Job이 실행 취소된 시점 수행됩니다");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.info("[+] Like Job이 실행 완료된 시점 수행됩니다");
    }
}
