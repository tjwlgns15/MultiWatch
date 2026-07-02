package com.sjh.multiwatch.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 디바이스
    DUPLICATE_DEVICE_KEY(CONFLICT, "이미 등록된 디바이스 키입니다.")


    ;
    private final HttpStatus status;
    private final String message;
}