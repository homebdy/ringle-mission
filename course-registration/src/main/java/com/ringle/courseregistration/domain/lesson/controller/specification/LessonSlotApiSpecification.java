package com.ringle.courseregistration.domain.lesson.controller.specification;

import com.ringle.courseregistration.domain.lesson.controller.dto.request.LessonSlotCreateRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.request.TimeUnitFindRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.request.TutorFindRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TimeUnitFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TutorFindResponse;
import com.ringle.courseregistration.global.constant.SwaggerTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface LessonSlotApiSpecification {

    @Tag(name = SwaggerTag.TUTOR_API)
    @Operation(summary = "튜터 - 수업 시간 추가")
    List<LessonSlotCreateResponse> save(Long memberId, @RequestBody List<LessonSlotCreateRequest> request);

    @Tag(name = SwaggerTag.TUTOR_API)
    @Operation(summary = "튜터 - 수업 시간 삭제")
    void delete(Long memberId, @PathVariable Long lessonSlotId);

    @Tag(name = SwaggerTag.LESSON_SLOT_API)
    @Operation(summary = "날짜, 수업 길이로 수업 시간대 조회")
    TimeUnitFindResponse findTimeUnitByDateAndLessonLength(@RequestBody TimeUnitFindRequest request);

    @Tag(name = SwaggerTag.LESSON_SLOT_API)
    @Operation(summary = "날짜, 시간대, 수업 길이로 수업 시간대 조회")
    TutorFindResponse findTutorByTimeAndDuration(@RequestBody TutorFindRequest request);
}
