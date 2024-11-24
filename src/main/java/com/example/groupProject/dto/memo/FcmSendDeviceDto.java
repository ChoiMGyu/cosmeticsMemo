package com.example.groupProject.dto.memo;

import java.time.LocalDate;

public record FcmSendDeviceDto(String deviceToken, String cosmeticName, String writer, LocalDate endDate) {
}
