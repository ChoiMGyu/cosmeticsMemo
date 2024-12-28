package com.example.groupProject.service.board;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

public class LikeJob implements Job {
    private static final String LIKE_JOB_ERROR_MESSAGE = "좋아요 스케줄러가 정상적으로 동작하지 않았습니다.";
    private static final String APPLICATION_CONTEXT_KEY = "appContext";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getMergedJobDataMap();

        ApplicationContext applicationContext = (ApplicationContext) dataMap.get(APPLICATION_CONTEXT_KEY);

        LikesService likesService = applicationContext.getBean(LikesService.class);

        try {
            likesService.syncLikesToDatabase();
        } catch (Exception e) {
            throw new JobExecutionException(LIKE_JOB_ERROR_MESSAGE, e);
        }
    }
}
