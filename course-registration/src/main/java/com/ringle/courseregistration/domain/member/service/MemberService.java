package com.ringle.courseregistration.domain.member.service;

import com.ringle.courseregistration.domain.member.controller.dto.response.MemberCreateResponse;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.repository.MemberRepository;
import com.ringle.courseregistration.domain.member.service.dto.MemberCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberCreateResponse save(final MemberCreateCommand command) {
        final Member member = memberRepository.save(new Member(command.name(), command.role()));

        return new MemberCreateResponse(
                member.getId(),
                member.getName(),
                member.getRole()
        );
    }
}
