package com.ringle.courseregistration.domain.lesson.service.dto;

import com.ringle.courseregistration.domain.lesson.entity.LessonInterval;

import java.time.LocalDateTime;

public record TutorFindCommand(LocalDateTime startAt, LessonInterval lessonInterval) {
}
