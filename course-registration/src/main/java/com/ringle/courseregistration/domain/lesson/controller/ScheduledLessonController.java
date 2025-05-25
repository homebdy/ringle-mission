package com.ringle.courseregistration.domain.lesson.controller;

import com.ringle.courseregistration.domain.lesson.controller.dto.request.ScheduledLessonCreateRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.ScheduledLessonFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.specification.ScheduledLessonApiSpecification;
import com.ringle.courseregistration.domain.lesson.service.ScheduledLessonService;
import com.ringle.courseregistration.domain.lesson.service.dto.ScheduleLessonCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/scheduled-lessons")
public class ScheduledLessonController implements ScheduledLessonApiSpecification {

    private final ScheduledLessonService scheduledLessonService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createScheduledLesson(@RequestBody ScheduledLessonCreateRequest request) {
        scheduledLessonService.createScheduledLesson(
                new ScheduleLessonCreateCommand(
                        request.memberId(),
                        request.startAt(),
                        request.lessonInterval(),
                        request.tutorId()
                )
        );
    }

    @GetMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ScheduledLessonFindResponse> findByMemberId(@PathVariable Long memberId) {
        return scheduledLessonService.findByMemberId(memberId);
    }
}
