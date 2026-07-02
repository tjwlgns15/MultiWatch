package com.sjh.multiwatch.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 디바이스
    DUPLICATE_DEVICE_KEY(CONFLICT, "이미 등록된 디바이스 키입니다."),
    DEVICE_NOT_FOUND(NOT_FOUND, "존재하지 않는 디바이스입니다."),

    // 알림 규칙
    ALERT_RULE_NOT_FOUND(NOT_FOUND, "존재하지 않는 알림 규칙입니다.")



    ;
    private final HttpStatus status;
    private final String message;
}