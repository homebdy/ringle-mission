package com.ringle.courseregistration.domain.member.controller;

import com.ringle.courseregistration.domain.member.controller.dto.request.MemberCreateRequest;
import com.ringle.courseregistration.domain.member.controller.dto.response.MemberCreateResponse;
import com.ringle.courseregistration.domain.member.controller.specification.MemberApiSpecification;
import com.ringle.courseregistration.domain.member.service.MemberService;
import com.ringle.courseregistration.domain.member.service.dto.MemberCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/members")
public class MemberController implements MemberApiSpecification {

    private final MemberService memberService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberCreateResponse create(@RequestBody final MemberCreateRequest request) {
        return memberService.save(
                new MemberCreateCommand(request.name(), request.role())
        );
    }
}
