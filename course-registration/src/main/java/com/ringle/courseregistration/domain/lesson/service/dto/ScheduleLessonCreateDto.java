package com.ringle.courseregistration.domain.lesson.service.dto;

import com.ringle.courseregistration.domain.lesson.entity.Duration;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleLessonCreateDto(
        Long memberId,
        LocalDate date,
        LocalTime startAt,
        Duration duration,
        Long tutorId
) {
}
