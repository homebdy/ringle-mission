package com.ringle.courseregistration.domain.tutor.repository;

import com.ringle.courseregistration.domain.tutor.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByMemberId(Long memberId);
}
