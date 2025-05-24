package com.ringle.courseregistration.domain.member.service;

import com.ringle.courseregistration.domain.member.controller.dto.response.MemberCreateResponse;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.mapper.MemberMapper;
import com.ringle.courseregistration.domain.member.repository.MemberRepository;
import com.ringle.courseregistration.domain.member.service.dto.MemberCreateDto;
import com.ringle.courseregistration.domain.student.repository.StudentRepository;
import com.ringle.courseregistration.domain.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final MemberMapper memberMapper;

    @Transactional
    public MemberCreateResponse save(final MemberCreateDto dto) {
        final Member member = memberRepository.save(memberMapper.toMember(dto));
        saveMemberByRole(member);
        return memberMapper.toMemberCreateResponse(member);
    }

    private void saveMemberByRole(final Member member) {
        if (member.isTutor()) {
            tutorRepository.save(memberMapper.toTutor(member));
            return;
        }
        studentRepository.save(memberMapper.toStudent(member));
    }
}
