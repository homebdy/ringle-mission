package com.ringle.courseregistration.domain.lesson.controller.dto.response;

import java.util.Collection;

public record TutorFindResponse(Collection<TutorTimeUnitResponse> timeUnitResponses) {
}
