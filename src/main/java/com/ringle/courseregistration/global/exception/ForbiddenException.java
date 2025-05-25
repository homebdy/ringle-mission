package com.ringle.courseregistration.global.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("권한이 없는 사용자입니다.");
    }
}