package com.ringle.courseregistration.domain.lesson.controller.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonSlotCreateResponse(
        Long id,
        LocalDate date,
        LocalTime startAt,
        Long tutorId,
        boolean isAvailable
) {
}
