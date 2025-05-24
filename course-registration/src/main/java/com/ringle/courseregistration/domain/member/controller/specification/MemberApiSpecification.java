package com.ringle.courseregistration.domain.member.controller.specification;

import com.ringle.courseregistration.domain.member.controller.dto.request.MemberCreateRequest;
import com.ringle.courseregistration.domain.member.controller.dto.response.MemberCreateResponse;
import com.ringle.courseregistration.global.constant.SwaggerTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberApiSpecification {

    @Tag(name = SwaggerTag.MEMBER_API)
    @Operation(summary = "사용자 추가")
    MemberCreateResponse create(@RequestBody final MemberCreateRequest request);
}
