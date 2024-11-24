package com.example.groupProject.controller.message;

public enum ErrorMessage {
    LOGIN_REQUIRED_MESSAGE("로그인이 필요합니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
