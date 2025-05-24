package com.ringle.courseregistration.domain.lesson.exception;

import com.ringle.courseregistration.global.exception.DataNotFoundException;

public class TimeUnitNotFoundException extends DataNotFoundException {

    public TimeUnitNotFoundException() {
        super("존재하지 않는 시간대입니다.");
    }
}
