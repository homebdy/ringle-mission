package com.ringle.courseregistration.domain.lesson.repository;

import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public interface LessonSlotRepository extends JpaRepository<LessonSlot, Long> {

    boolean existsByStartAtAndTutorId(LocalDateTime startAt, Long tutorId);

    @Lock(LockModeType.OPTIMISTIC)
    List<LessonSlot> findAllByTutorIdAndReservedAndStartAtIsBetween(Long tutorId, boolean reserved, LocalDateTime startAt, LocalDateTime endAt);

    List<LessonSlot> findAllByReservedAndStartAtIsBetween(boolean reserved, LocalDateTime startAt, LocalDateTime endAt);

    @Query("SELECT l FROM LessonSlot l WHERE l.reserved = :reserved AND FUNCTION('date', l.startAt) = :date")
    List<LessonSlot> findAllByDate(@Param("reserved") boolean reserved, @Param("date") LocalDate date);
}
