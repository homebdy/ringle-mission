package com.ringle.courseregistration.domain.lesson.service.dto;

import com.ringle.courseregistration.domain.lesson.entity.Duration;

import java.time.LocalDate;

public record TimeUnitFindDto(LocalDate date, Duration duration) {
}
