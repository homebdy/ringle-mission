package com.ringle.courseregistration.domain.lesson.controller.dto.response;

import java.time.LocalDateTime;
import java.util.Collection;

public record TutorTimeUnitResponse(Long tutorId, String tutorName, Collection<LocalDateTime> times) {
}
