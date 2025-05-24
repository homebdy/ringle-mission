package com.ringle.courseregistration.domain.member.controller.dto.response;

import com.ringle.courseregistration.domain.member.entity.Role;

public record MemberCreateResponse(Long id, String nickname, Role role) {
}
