package com.ringle.courseregistration.domain.lesson.exception;

import com.ringle.courseregistration.global.exception.BadRequestException;

public class InvalidDateException extends BadRequestException {

    public InvalidDateException() {
        super("오늘보다 이전 날짜의 수업은 생성할 수 없습니다.");
    }
}
