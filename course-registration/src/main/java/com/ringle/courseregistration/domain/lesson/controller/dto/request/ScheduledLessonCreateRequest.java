package com.ringle.courseregistration.domain.lesson.controller.dto.request;

import com.ringle.courseregistration.domain.lesson.entity.LessonInterval;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ScheduledLessonCreateRequest(
        @Schema(example = "2") Long memberId,
        @Schema(example = "2025-06-25T00:00:00", type = "string", description = "Date time") LocalDateTime startAt,
        @Schema(example = "MINUTES_60") LessonInterval lessonInterval,
        @Schema(example = "1") Long tutorId
) {
}
