package com.ringle.courseregistration.domain.student.exception;

import com.ringle.courseregistration.global.exception.DataNotFoundException;

public class StudentNotFoundException extends DataNotFoundException {

    public StudentNotFoundException() {
        super("존재하지 않는 수강생 정보입니다.");
    }
}
