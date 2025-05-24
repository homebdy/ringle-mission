package com.ringle.courseregistration.domain.member.service.dto;

import com.ringle.courseregistration.domain.member.entity.Role;

public record MemberCreateDto(String name, Role role) {
}
