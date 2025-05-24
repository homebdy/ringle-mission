package com.ringle.courseregistration.domain.lesson.entity;

import com.ringle.courseregistration.domain.lesson.constant.LessonConstant;
import lombok.Getter;

@Getter
public enum Duration {

    MINUTES_30(LessonConstant.LESSON_LENGTH),
    MINUTES_60(LessonConstant.LESSON_LENGTH * 2);

    private final int lessonLength;

    Duration(int lessonLength) {
        this.lessonLength = lessonLength;
    }
}
