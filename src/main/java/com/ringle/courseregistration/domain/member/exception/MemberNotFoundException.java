package com.ringle.courseregistration.domain.member.exception;

import com.ringle.courseregistration.global.exception.DataNotFoundException;

public class MemberNotFoundException extends DataNotFoundException {

    public MemberNotFoundException() {
        super("존재하지 않거나 접근할 수 없는 사용자입니다.");
    }
}
