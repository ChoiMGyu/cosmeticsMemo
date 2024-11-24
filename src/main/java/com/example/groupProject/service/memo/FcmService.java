package com.example.groupProject.service.memo;

import com.example.groupProject.domain.memo.Memo;
import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.User;
import com.example.groupProject.dto.memo.FcmSendDeviceDto;
import com.example.groupProject.dto.memo.FcmSendDto;

import java.io.IOException;
import java.util.List;

public interface FcmService {

    Long saveDeviceToken(String deviceToken, User user);

    void sendMessageTo(FcmSendDto fcmSendDto) throws IOException;

    List<FcmSendDeviceDto> selectFcmSendList();
}
