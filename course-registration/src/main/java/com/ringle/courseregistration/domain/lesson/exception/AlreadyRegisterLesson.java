package com.ringle.courseregistration.domain.lesson.exception;

import com.ringle.courseregistration.global.exception.DataExistException;

public class AlreadyRegisterLesson extends DataExistException {

    public AlreadyRegisterLesson() {
        super("이미 선정된 수업입니다.");
    }
}
