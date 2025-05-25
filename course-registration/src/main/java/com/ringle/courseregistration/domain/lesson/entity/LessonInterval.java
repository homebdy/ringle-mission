package com.ringle.courseregistration.domain.lesson.entity;

import com.ringle.courseregistration.domain.lesson.constant.LessonConstant;
import lombok.Getter;

@Getter
public enum LessonInterval {

    MINUTES_30(LessonConstant.LESSON_LENGTH),
    MINUTES_60(LessonConstant.LESSON_LENGTH * 2);

    private final int lessonLength;

    LessonInterval(int lessonLength) {
        this.lessonLength = lessonLength;
    }
}
