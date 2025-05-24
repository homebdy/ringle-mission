package com.ringle.courseregistration.domain.lesson.entity;

import com.ringle.courseregistration.domain.student.entity.Student;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LessonSlot lessonSlot;

    @Builder
    public ScheduledLesson(Long id, LessonSlot lessonSlot, Student student) {
        this.id = id;
        this.lessonSlot = lessonSlot;
        this.student = student;
    }

    @ManyToOne
    private Student student;
}
