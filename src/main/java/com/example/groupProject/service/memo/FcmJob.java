package com.example.groupProject.service.memo;

import com.example.groupProject.dto.memo.FcmSendDeviceDto;
import com.example.groupProject.dto.memo.FcmSendDto;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.List;

@Slf4j
public class FcmJob implements Job {

    private FcmService fcmService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (fcmService == null) {
            ApplicationContext appCtx = (ApplicationContext) jobExecutionContext.getJobDetail().getJobDataMap().get("appContext");
            fcmService = appCtx.getBean(FcmService.class);
        }

        List<FcmSendDeviceDto> selectFcmSendList = fcmService.selectFcmSendList();

        for (FcmSendDeviceDto fcmSendItem : selectFcmSendList) {
            FcmSendDto fcmSendDto = FcmSendDto.builder()
                    .token(fcmSendItem.deviceToken())
                    .title(fcmSendItem.cosmeticName() + "의 사용 기한이 지났어요!")
                    .body(fcmSendItem.writer() + "님의 화장품이 사용 기한 마지막 날짜(" + fcmSendItem.endDate().toString() + ")")
                    .build();
            try {
                fcmService.sendMessageTo(fcmSendDto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
