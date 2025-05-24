package com.ringle.courseregistration.domain.member.mapper;

import com.ringle.courseregistration.domain.member.controller.dto.response.MemberCreateResponse;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.service.dto.MemberCreateDto;
import com.ringle.courseregistration.domain.student.entity.Student;
import com.ringle.courseregistration.domain.tutor.entity.Tutor;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public Member toMember(MemberCreateDto dto) {
        return Member.builder()
                .name(dto.name())
                .role(dto.role())
                .build();
    }

    public MemberCreateResponse toMemberCreateResponse(Member member) {
        return new MemberCreateResponse(
                member.getId(),
                member.getName(),
                member.getRole()
        );
    }

    public Tutor toTutor(Member member) {
        return Tutor.builder()
                .member(member)
                .build();
    }

    public Student toStudent(Member member) {
        return Student.builder()
                .member(member)
                .build();
    }
}
