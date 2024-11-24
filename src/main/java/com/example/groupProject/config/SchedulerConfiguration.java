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

        JobDataMap ctx = new JobDataMap();
        ctx.put(APPLICATION_NAME, applicationContext);

        JobDetail job = JobBuilder
                .newJob(FcmJob.class)
                .withIdentity("fcmSendJob", "fcmGroup")
                .withDescription("FCM 처리를 위한 조회 Job")
                .setJobData(ctx)
                .build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("fcmSendTrigger", "fcmGroup")
                .withDescription("FCM 처리를 위한 조회 Trigger")
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInSeconds(10)
                                .repeatForever())
                .build();

        scheduler = new StdSchedulerFactory().getScheduler();
        FcmJobListener fcmJobListener = new FcmJobListener();
        scheduler.getListenerManager().addJobListener(fcmJobListener);
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }
}
