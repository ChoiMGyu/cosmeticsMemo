package com.example.groupProject.config;

import com.example.groupProject.service.memo.FcmJob;
import com.example.groupProject.service.memo.FcmJobListener;
import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SchedulerConfiguration implements WebMvcConfigurer {

    private static String APPLICATION_NAME = "appContext";

    private Scheduler scheduler;
    private final ApplicationContext applicationContext;

    public SchedulerConfiguration(Scheduler sch, ApplicationContext applicationContext) {
        this.scheduler = sch;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void configScheduler() throws SchedulerException {

        JobDataMap ctx = new JobDataMap();                  // 스케줄러에게 애플리케이션 영역을 추가합니다.
        ctx.put(APPLICATION_NAME, applicationContext);      // 애플리케이션 영역을 "appContext"으로 지정합니다.

        // [STEP1] Job 생성
        JobDetail job = JobBuilder
                .newJob(FcmJob.class)                                   // Job 구현 클래스
                .withIdentity("fcmSendJob", "fcmGroup")     // Job 이름, 그룹 지정
                .withDescription("FCM 처리를 위한 조회 Job")   // Job 설명
                .setJobData(ctx)
                .build();

        // [STEP2] Trigger 생성
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("fcmSendTrigger", "fcmGroup")         // Trigger 이름, 그룹 지정
                .withDescription("FCM 처리를 위한 조회 Trigger")     // Trigger 설명
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInSeconds(10)
                                .repeatForever())
                .build();

        // [STEP3] 스케줄러 생성 및 Job, Trigger 등록
        scheduler = new StdSchedulerFactory().getScheduler();
        FcmJobListener fcmJobListener = new FcmJobListener();
        scheduler.getListenerManager().addJobListener(fcmJobListener);
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }
}
