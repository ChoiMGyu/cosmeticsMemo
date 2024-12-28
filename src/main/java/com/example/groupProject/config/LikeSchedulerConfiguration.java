package com.example.groupProject.config;

import com.example.groupProject.service.board.LikeJob;
import com.example.groupProject.service.board.LikeJobListener;
import jakarta.annotation.PostConstruct;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnProperty(name = "likeScheduler.enabled", havingValue = "true", matchIfMissing = false)
public class LikeSchedulerConfiguration implements WebMvcConfigurer {
    private static final int JOB_INTERVAL_MINUTE = 1;
    private static final String APPLICATION_NAME = "appContext";

    private Scheduler scheduler;
    private final ApplicationContext applicationContext;

    public LikeSchedulerConfiguration(Scheduler scheduler, ApplicationContext applicationContext) {
        this.scheduler = scheduler;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void scheduleLikeWriteJob() throws SchedulerException {
        JobDataMap ctx = new JobDataMap();
        ctx.put(APPLICATION_NAME, applicationContext);

        JobDetail job = JobBuilder.newJob(LikeJob.class)
                .withIdentity("likeWriteJob", "likeGroup")
                .withDescription("LIKE의 수를 DB에 저장 Job")
                .setJobData(ctx)
                .build();

        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("likeWriteTrigger", "likeGroup")
                .withDescription("LIKE의 수를 DB에 저장 Trigger")
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder
                                .simpleSchedule()
                                .withIntervalInMinutes(JOB_INTERVAL_MINUTE)
                                .repeatForever())
                .build();

        scheduler = new StdSchedulerFactory().getScheduler();
        LikeJobListener likeJobListener = new LikeJobListener();
        scheduler.getListenerManager().addJobListener(likeJobListener);
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }
}
