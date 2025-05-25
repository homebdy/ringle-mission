package com.ringle.courseregistration.domain.lesson.controller.dto.request;

import com.ringle.courseregistration.domain.lesson.entity.LessonInterval;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record TimeUnitFindRequest(
        @Schema(example = "2025-06-25", type = "string", description = "Date time") LocalDate date,
        @Schema(example = "MINUTES_30") LessonInterval lessonInterval
) {
}
