package com.ringle.courseregistration.domain.lesson.entity;

import com.ringle.courseregistration.domain.tutor.entity.Tutor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LessonSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne
    private TimeUnit timeUnit;

    @ManyToOne
    private Tutor tutor;

    private boolean isAvailable;

    @Builder
    public LessonSlot(Long id, LocalDate date, TimeUnit timeUnit, Tutor tutor, boolean isAvailable) {
        this.id = id;
        this.date = date;
        this.timeUnit = timeUnit;
        this.tutor = tutor;
        this.isAvailable = isAvailable;
    }
}
