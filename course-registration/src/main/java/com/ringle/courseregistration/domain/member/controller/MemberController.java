package com.ringle.courseregistration.domain.member.controller;

import com.ringle.courseregistration.domain.member.controller.dto.request.MemberCreateRequest;
import com.ringle.courseregistration.domain.member.controller.dto.response.MemberCreateResponse;
import com.ringle.courseregistration.domain.member.service.MemberService;
import com.ringle.courseregistration.domain.member.service.dto.MemberCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberCreateResponse create(@RequestBody final MemberCreateRequest request) {
        return memberService.save(
                new MemberCreateDto(request.name(), request.role())
        );
    }
}
