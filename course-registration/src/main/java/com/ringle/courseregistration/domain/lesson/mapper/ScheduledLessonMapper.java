package com.ringle.courseregistration.domain.lesson.mapper;

import com.ringle.courseregistration.domain.lesson.controller.dto.response.ScheduledLessonFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TutorResponse;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.ScheduledLesson;
import com.ringle.courseregistration.domain.student.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class ScheduledLessonMapper {

    public ScheduledLesson toScheduledLesson(LessonSlot slot, Student student) {
        return ScheduledLesson.builder()
                .lessonSlot(slot)
                .student(student)
                .build();
    }

    public ScheduledLessonFindResponse toScheduledLessonFindResponse(ScheduledLesson lesson) {
        return new ScheduledLessonFindResponse(
                lesson.getId(),
                lesson.getLessonSlot().getDate(),
                lesson.getLessonSlot().getStartTime(),
                new TutorResponse(lesson.getLessonSlot().getTutor().getId(), lesson.getLessonSlot().getTutor().getMember().getName())
        );
    }
}
