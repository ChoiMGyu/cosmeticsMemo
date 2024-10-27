package com.example.groupProject.service.memo;

import com.example.groupProject.dto.memo.FcmSendDto;

import java.io.IOException;

public interface FcmService {

    public int sendMessageTo(FcmSendDto fcmSendDto) throws IOException;
}
