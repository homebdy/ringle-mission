package com.ringle.courseregistration.domain.lesson.repository;

import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.TimeUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public interface LessonSlotRepository extends JpaRepository<LessonSlot, Long> {

    boolean existsByDateAndTimeUnitAndTutorId(LocalDate date, TimeUnit timeUnit, Long tutorId);
    List<LessonSlot> findByDate(LocalDate date);
}
