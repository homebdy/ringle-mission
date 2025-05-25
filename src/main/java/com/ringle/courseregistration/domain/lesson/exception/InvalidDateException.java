package com.ringle.courseregistration.domain.lesson.exception;

import com.ringle.courseregistration.global.exception.BadRequestException;

public class InvalidDateException extends BadRequestException {

    public InvalidDateException() {
        super("유효하지 않은 시간 단위입니다.");
    }
}
