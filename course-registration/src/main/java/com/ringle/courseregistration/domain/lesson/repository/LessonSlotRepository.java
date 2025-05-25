package com.ringle.courseregistration.domain.lesson.repository;

import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public interface LessonSlotRepository extends JpaRepository<LessonSlot, Long> {

    boolean existsByStartAtAndTutorId(LocalDateTime startAt, Long tutorId);

    Optional<LessonSlot> findByStartAtAndTutorId(LocalDateTime startAt, Long tutorId);

    List<LessonSlot> findAllByTutorIdAndReservedAndStartAtIsBetween(Long tutorId, boolean reserved, LocalDateTime startAt, LocalDateTime endAt);

    List<LessonSlot> findAllByReservedAndStartAtIsBetween(boolean reserved, LocalDateTime startAt, LocalDateTime endAt);

    @Query("SELECT l FROM LessonSlot l WHERE l.reserved = :reserved AND FUNCTION('date', l.startAt) = :date")
    List<LessonSlot> findAllByDate(@Param("reserved") boolean reserved, @Param("date") LocalDate date);
}
