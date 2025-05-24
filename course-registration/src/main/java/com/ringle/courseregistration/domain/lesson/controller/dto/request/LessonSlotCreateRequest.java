package com.ringle.courseregistration.domain.lesson.controller.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonSlotCreateRequest(
        LocalDate date,
        LocalTime startAt
) {
}
