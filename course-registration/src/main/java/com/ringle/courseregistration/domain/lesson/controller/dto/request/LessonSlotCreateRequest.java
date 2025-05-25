package com.ringle.courseregistration.domain.lesson.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record LessonSlotCreateRequest(
        @Schema(example = "2025-06-25T00:00:00", type = "string", description = "Date time") LocalDateTime startAt
) {
}
