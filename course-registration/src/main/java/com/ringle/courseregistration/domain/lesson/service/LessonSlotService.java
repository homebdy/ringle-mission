package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.TimeUnit;
import com.ringle.courseregistration.domain.lesson.mapper.LessonSlotMapper;
import com.ringle.courseregistration.domain.lesson.repository.LessonSlotRepository;
import com.ringle.courseregistration.domain.lesson.repository.TimeUnitRepository;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotCreateDto;
import com.ringle.courseregistration.domain.tutor.entity.Tutor;
import com.ringle.courseregistration.domain.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LessonSlotService {

    private final LessonSlotRepository lessonSlotRepository;
    private final TimeUnitRepository timeUnitRepository;
    private final TutorRepository tutorRepository;
    private final LessonSlotMapper lessonSlotMapper;

    public List<LessonSlotCreateResponse> save(LessonSlotCreateDto dto) {
        final Tutor tutor = tutorRepository.findByMemberId(dto.memberId())
                .orElseThrow(() -> new RuntimeException("Tutor not found"));

        return dto.slots().stream()
                .map(slot -> {
                    final TimeUnit timeUnit = timeUnitRepository.findByStartAt(slot.startAt())
                            .orElseThrow(() -> new RuntimeException("TimeUnit not found"));
                    LessonSlot savedSlot = lessonSlotRepository.save(lessonSlotMapper.toLessonSlot(slot, timeUnit, tutor));
                    return lessonSlotMapper.toLessonSlotCreateResponse(savedSlot);
                })
                .toList();
    }
}
