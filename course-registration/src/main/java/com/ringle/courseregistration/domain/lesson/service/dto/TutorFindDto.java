package com.ringle.courseregistration.domain.lesson.service.dto;

import com.ringle.courseregistration.domain.lesson.entity.Duration;

import java.time.LocalDate;
import java.time.LocalTime;

public record TutorFindDto(LocalDate date, LocalTime startAt, Duration duration) {
}
