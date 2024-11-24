package com.example.groupProject.repository.memo;

import com.example.groupProject.domain.memo.Memo;
import com.example.groupProject.domain.memo.Skincare;
import com.example.groupProject.domain.user.DeviceToken;

import java.util.List;

public interface FcmRepository {

    void save(DeviceToken deviceToken);

    List<Memo> findByEndDateAfter();

    List<String> findDeviceTokensByMemo(Long userId);
}
