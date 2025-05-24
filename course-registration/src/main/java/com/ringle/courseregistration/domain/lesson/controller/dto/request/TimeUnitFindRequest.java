package com.ringle.courseregistration.domain.lesson.controller.dto.request;

import com.ringle.courseregistration.domain.lesson.entity.Duration;

import java.time.LocalDate;

public record TimeUnitFindRequest(LocalDate date, Duration duration) {
}
