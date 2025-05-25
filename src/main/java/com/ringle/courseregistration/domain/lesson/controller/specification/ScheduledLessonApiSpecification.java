package com.ringle.courseregistration.domain.lesson.controller.specification;

import com.ringle.courseregistration.domain.lesson.controller.dto.request.ScheduledLessonCreateRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.ScheduledLessonFindResponse;
import com.ringle.courseregistration.global.constant.SwaggerTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

public interface ScheduledLessonApiSpecification {

    @Tag(name = SwaggerTag.SCHEDULED_API)
    @Operation(summary = "새로운 수업 신청")
    void createScheduledLesson(@RequestBody ScheduledLessonCreateRequest request);

    @Tag(name = SwaggerTag.SCHEDULED_API)
    @Operation(summary = "수강 내역 조회")
    Collection<ScheduledLessonFindResponse> findByMemberId(@PathVariable Long memberId);
}
