package com.ringle.courseregistration.domain.lesson.controller;

import com.ringle.courseregistration.domain.lesson.controller.dto.request.ScheduledLessonCreateRequest;
import com.ringle.courseregistration.domain.lesson.service.ScheduledLessonService;
import com.ringle.courseregistration.domain.lesson.service.dto.ScheduleLessonCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/scheduled-lessons")
public class ScheduledLessonController {

    private final ScheduledLessonService scheduledLessonService;

    @PostMapping
    public void createScheduledLesson(@RequestBody ScheduledLessonCreateRequest request) {
        scheduledLessonService.createScheduledLesson(
            new ScheduleLessonCreateDto(
                    request.memberId(),
                    request.date(),
                    request.startAt(),
                    request.duration(),
                    request.tutorId()
            )
        );
    }
}
