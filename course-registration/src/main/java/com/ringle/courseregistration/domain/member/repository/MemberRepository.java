package com.ringle.courseregistration.domain.member.repository;

import com.ringle.courseregistration.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface MemberRepository extends JpaRepository<Member, Long> {
}
