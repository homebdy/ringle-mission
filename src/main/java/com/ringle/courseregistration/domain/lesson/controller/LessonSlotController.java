package com.ringle.courseregistration.domain.lesson.controller;

import com.ringle.courseregistration.domain.lesson.controller.dto.request.LessonSlotCreateRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.request.TimeUnitFindRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.request.TutorFindRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TimeUnitFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TutorFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.specification.LessonSlotApiSpecification;
import com.ringle.courseregistration.domain.lesson.service.LessonSlotService;
import com.ringle.courseregistration.domain.lesson.service.dto.AvailableTimeFindCommand;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotCreateCommand;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotDeleteCommand;
import com.ringle.courseregistration.domain.lesson.service.dto.TutorFindCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/slots")
public class LessonSlotController implements LessonSlotApiSpecification {

    private final LessonSlotService lessonSlotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<LessonSlotCreateResponse> save(
            @RequestParam Long memberId,
            @RequestBody List<LessonSlotCreateRequest> request
    ) {
        return lessonSlotService.save(
                new LessonSlotCreateCommand(
                        memberId,
                        request
                )
        );
    }

    @DeleteMapping("/{lessonSlotId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam Long memberId, @PathVariable Long lessonSlotId) {
        lessonSlotService.delete(
                new LessonSlotDeleteCommand(memberId, lessonSlotId)
        );
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public TimeUnitFindResponse findTimeUnitByDateAndLessonLength(@ModelAttribute TimeUnitFindRequest request) {
        return lessonSlotService.findTimeUnitByDateAndLessonLength(
                new AvailableTimeFindCommand(
                        request.date(),
                        request.lessonInterval()
                )
        );
    }

    @GetMapping("/tutors")
    @ResponseStatus(HttpStatus.OK)
    public TutorFindResponse findTutorByTimeAndlessonInterval(@ModelAttribute TutorFindRequest request) {
        return lessonSlotService.findTutorByTimeAndLessonInterval(
                new TutorFindCommand(
                        request.startAt(),
                        request.lessonInterval()
                )
        );
    }
}
