package com.ringle.courseregistration.domain.lesson.controller.dto.request;

import com.ringle.courseregistration.domain.lesson.entity.Duration;

import java.time.LocalDate;
import java.time.LocalTime;

public record TutorFindRequest(LocalDate date, LocalTime startAt, Duration duration) {
}
