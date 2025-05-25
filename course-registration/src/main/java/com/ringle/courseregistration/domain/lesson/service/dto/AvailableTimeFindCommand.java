package com.ringle.courseregistration.domain.lesson.service.dto;

import com.ringle.courseregistration.domain.lesson.entity.LessonInterval;

import java.time.LocalDate;

public record AvailableTimeFindCommand(LocalDate date, LessonInterval lessonInterval) {

}
