package com.ringle.courseregistration.domain.lesson.entity;

import com.ringle.courseregistration.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private LessonSlot lessonSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member student;

    public ScheduledLesson(LessonSlot lessonSlot, Member student) {
        this.lessonSlot = lessonSlot;
        this.student = student;
    }

    public Member getTutor() {
        return lessonSlot.getTutor();
    }
}
