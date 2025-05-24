package com.ringle.courseregistration.domain.lesson.mapper;

import com.ringle.courseregistration.domain.lesson.controller.dto.request.LessonSlotCreateRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.TimeUnit;
import com.ringle.courseregistration.domain.tutor.entity.Tutor;
import org.springframework.stereotype.Component;

@Component
public class LessonSlotMapper {

    public LessonSlot toLessonSlot(LessonSlotCreateRequest dto, TimeUnit timeUnit, Tutor tutor) {
        return LessonSlot.builder()
                .date(dto.date())
                .timeUnit(timeUnit)
                .tutor(tutor)
                .isAvailable(false)
                .build();
    }

    public LessonSlotCreateResponse toLessonSlotCreateResponse(LessonSlot slot) {
        return new LessonSlotCreateResponse(
                slot.getId(),
                slot.getDate(),
                slot.getTimeUnit().getStartAt(),
                slot.getTutor().getId(),
                slot.isAvailable()
        );
    }
}
