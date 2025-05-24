package com.ringle.courseregistration.domain.lesson.repository;

import com.ringle.courseregistration.domain.lesson.entity.TimeUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Optional;

@Component
public interface TimeUnitRepository extends JpaRepository<TimeUnit, Long> {

    Optional<TimeUnit> findByStartAt(LocalTime startAt);
}
