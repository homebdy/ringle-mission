package com.ringle.courseregistration.domain.lesson.controller.dto.response;

import java.time.LocalDateTime;

public record LessonSlotCreateResponse(
        Long id,
        LocalDateTime startAt,
        Long tutorId,
        boolean reserved
) {
}
