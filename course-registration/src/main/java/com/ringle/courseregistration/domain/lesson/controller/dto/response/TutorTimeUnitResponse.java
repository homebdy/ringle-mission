package com.ringle.courseregistration.domain.lesson.controller.dto.response;

import java.time.LocalTime;
import java.util.Collection;

public record TutorTimeUnitResponse(Long tutorId, String tutorName, Collection<LocalTime> times) {
}
