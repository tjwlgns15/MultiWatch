package com.sjh.multiwatch.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 디바이스
    DUPLICATE_DEVICE_KEY(CONFLICT, "이미 등록된 디바이스 키입니다."),
    DEVICE_NOT_FOUND(NOT_FOUND, "존재하지 않는 디바이스입니다."),

    // 알림 규칙
    ALERT_RULE_NOT_FOUND(NOT_FOUND, "존재하지 않는 알림 규칙입니다."),

    // 알림
    ALERT_NOT_FOUND(NOT_FOUND, "존재하지 않는 알림입니다."),

    // 회원
    DUPLICATE_EMAIL(CONFLICT, "이미 사용 중인 이메일입니다."),
    MEMBER_FORBIDDEN(FORBIDDEN, "관리자만 팀원을 추가할 수 있습니다."),

    ;
    private final HttpStatus status;
    private final String message;
}