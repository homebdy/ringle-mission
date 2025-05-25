package com.ringle.courseregistration.domain.member.repository;

import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByIdAndRole(Long id, Role role);
}
