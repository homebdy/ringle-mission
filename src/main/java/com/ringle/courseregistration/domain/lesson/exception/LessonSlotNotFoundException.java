package com.ringle.courseregistration.domain.lesson.exception;

import com.ringle.courseregistration.global.exception.DataNotFoundException;

public class LessonSlotNotFoundException extends DataNotFoundException {

    public LessonSlotNotFoundException() {
        super("수업 정보를 찾을 수 없습니다.");
    }
}
