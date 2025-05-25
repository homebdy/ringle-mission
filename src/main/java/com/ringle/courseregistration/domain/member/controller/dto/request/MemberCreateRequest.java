package com.ringle.courseregistration.domain.member.controller.dto.request;

import com.ringle.courseregistration.domain.member.entity.Role;

public record MemberCreateRequest(
        String name,
        Role role
) {
}
