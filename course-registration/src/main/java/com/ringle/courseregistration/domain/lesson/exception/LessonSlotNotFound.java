package com.ringle.courseregistration.domain.lesson.exception;

import com.ringle.courseregistration.global.exception.DataNotFoundException;

public class LessonSlotNotFound extends DataNotFoundException {

    public LessonSlotNotFound() {
        super("존재하지 않는 강의입니다.");
    }
}
