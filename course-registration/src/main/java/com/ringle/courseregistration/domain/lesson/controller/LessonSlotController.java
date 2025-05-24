package com.ringle.courseregistration.domain.lesson.controller;

import com.ringle.courseregistration.domain.lesson.controller.dto.request.LessonSlotCreateRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.service.LessonSlotService;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotCreateDto;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotDeleteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/slots")
public class LessonSlotController {

    private final LessonSlotService lessonSlotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<LessonSlotCreateResponse> save(Long memberId, @RequestBody List<LessonSlotCreateRequest> request) {
        return lessonSlotService.save(
                new LessonSlotCreateDto(
                        memberId,
                        request
                )
        );
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping("/{lessonSlotId}")
    public void delete(Long memberId, @PathVariable Long lessonSlotId) {
        lessonSlotService.delete(
                new LessonSlotDeleteDto(memberId, lessonSlotId)
        );
    }
}
