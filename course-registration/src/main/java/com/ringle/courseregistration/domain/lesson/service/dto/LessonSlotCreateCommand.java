package com.ringle.courseregistration.domain.lesson.service.dto;

import com.ringle.courseregistration.domain.lesson.controller.dto.request.LessonSlotCreateRequest;

import java.util.List;

public record LessonSlotCreateCommand(
        Long memberId,
        List<LessonSlotCreateRequest> slots
) {
}
