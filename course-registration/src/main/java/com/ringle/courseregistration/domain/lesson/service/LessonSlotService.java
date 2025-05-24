package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.TimeUnit;
import com.ringle.courseregistration.domain.lesson.exception.InvalidDateException;
import com.ringle.courseregistration.domain.lesson.exception.LessonSlotAlreadyExist;
import com.ringle.courseregistration.domain.lesson.exception.TimeUnitNotFoundException;
import com.ringle.courseregistration.domain.lesson.mapper.LessonSlotMapper;
import com.ringle.courseregistration.domain.lesson.repository.LessonSlotRepository;
import com.ringle.courseregistration.domain.lesson.repository.TimeUnitRepository;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotCreateDto;
import com.ringle.courseregistration.domain.tutor.entity.Tutor;
import com.ringle.courseregistration.domain.tutor.exception.TutorNotFoundException;
import com.ringle.courseregistration.domain.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                .orElseThrow(TutorNotFoundException::new);;
        return dto.slots().stream()
                .map(slot -> {
                    final TimeUnit timeUnit = timeUnitRepository.findByStartAt(slot.startAt())
                            .orElseThrow(TimeUnitNotFoundException::new);
                    validateDate(slot.date());
                    validateLessonSlot(slot.date(), timeUnit, tutor.getId());
                    LessonSlot savedSlot = lessonSlotRepository.save(lessonSlotMapper.toLessonSlot(slot, timeUnit, tutor));
                    return lessonSlotMapper.toLessonSlotCreateResponse(savedSlot);
                })
                .toList();
    }

    private void validateDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidDateException();
        }
    }

    private void validateLessonSlot(LocalDate date, TimeUnit timeUnit, Long tutorId) {
        if (lessonSlotRepository.existsByDateAndTimeUnitAndTutorId(date, timeUnit, tutorId)) {
            throw new LessonSlotAlreadyExist();
        }
    }
}
