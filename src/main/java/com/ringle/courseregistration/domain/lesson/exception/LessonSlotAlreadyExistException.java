package com.ringle.courseregistration.domain.lesson.exception;

import com.ringle.courseregistration.global.exception.DataExistException;

public class LessonSlotAlreadyExistException extends DataExistException {

    public LessonSlotAlreadyExistException() {
        super("이미 해당 시간대의 수업이 열려있습니다.");
    }
}
