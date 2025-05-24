package com.ringle.courseregistration.domain.tutor.exception;

import com.ringle.courseregistration.global.exception.DataNotFoundException;

public class TutorNotFoundException extends DataNotFoundException {

    public TutorNotFoundException() {
        super("튜터 정보를 찾을 수 없습니다.");
    }
}
