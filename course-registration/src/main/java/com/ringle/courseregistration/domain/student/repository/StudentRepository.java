package com.ringle.courseregistration.domain.student.repository;

import com.ringle.courseregistration.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByMemberId(Long memberId);
}
