package com.ringle.courseregistration.domain.lesson.controller.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

public record TimeUnitFindResponse(LocalDate date, Collection<LocalTime> availableTimeUnits) {
}
