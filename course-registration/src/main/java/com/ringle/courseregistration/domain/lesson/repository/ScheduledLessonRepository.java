package com.ringle.courseregistration.domain.lesson.repository;

import com.ringle.courseregistration.domain.lesson.entity.ScheduledLesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledLessonRepository extends JpaRepository<ScheduledLesson, Long> {

    List<ScheduledLesson> findByStudentId(Long tutorId);
}
