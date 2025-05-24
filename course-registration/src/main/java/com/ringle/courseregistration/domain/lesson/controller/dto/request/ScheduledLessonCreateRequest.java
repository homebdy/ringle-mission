package com.ringle.courseregistration.domain.lesson.controller.dto.request;

import com.ringle.courseregistration.domain.lesson.entity.Duration;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduledLessonCreateRequest(
        Long memberId,
        LocalDate date,
        LocalTime startAt,
        Duration duration,
        Long tutorId
) {
}
