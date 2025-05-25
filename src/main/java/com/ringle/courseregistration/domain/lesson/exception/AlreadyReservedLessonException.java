package com.ringle.courseregistration.domain.lesson.exception;

import com.ringle.courseregistration.global.exception.DataExistException;

public class AlreadyReservedLessonException extends DataExistException {

    public AlreadyReservedLessonException() {
        super("이미 예약된 수업입니다.");
    }
}
