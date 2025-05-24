package com.ringle.courseregistration.domain.lesson.controller.dto.response;

import java.time.LocalDate;
import java.util.Collection;

public record TutorFindResponse(LocalDate date, Collection<TutorTimeUnitResponse> timeUnitResponses) {
}
