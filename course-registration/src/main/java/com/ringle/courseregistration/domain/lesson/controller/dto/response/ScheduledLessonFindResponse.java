package com.ringle.courseregistration.domain.lesson.controller.dto.response;

import java.time.LocalDateTime;

public record ScheduledLessonFindResponse(
        Long id,
        LocalDateTime startAt,
        TutorResponse tutor
) {
}
