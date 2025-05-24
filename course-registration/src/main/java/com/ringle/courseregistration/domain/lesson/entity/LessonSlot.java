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
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE lesson_slot SET delete_at = true WHERE id = ?")
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

    private LocalDateTime deleteAt;

    @Builder
    public LessonSlot(Long id, LocalDate date, TimeUnit timeUnit, Tutor tutor, boolean isAvailable) {
        this.id = id;
        this.date = date;
        this.timeUnit = timeUnit;
        this.tutor = tutor;
        this.isAvailable = isAvailable;
    }

    public boolean isSameTutor(Tutor tutor) {
        return this.tutor.equals(tutor);
    }

    public void delete() {
        this.deleteAt = LocalDateTime.now();
    }

    public LocalTime getStartTime() {
        return timeUnit.getStartAt();
    }

    public void register() {
        this.isAvailable = true;
    }

    public boolean isRegistered() {
        return isAvailable;
    }
}
