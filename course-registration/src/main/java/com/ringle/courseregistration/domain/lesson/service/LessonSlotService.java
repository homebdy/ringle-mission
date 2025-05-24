package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.TimeUnit;
import com.ringle.courseregistration.domain.lesson.exception.InvalidDateException;
import com.ringle.courseregistration.domain.lesson.exception.LessonSlotAlreadyExist;
import com.ringle.courseregistration.domain.lesson.exception.LessonSlotNotFound;
import com.ringle.courseregistration.domain.lesson.exception.TimeUnitNotFoundException;
import com.ringle.courseregistration.domain.lesson.mapper.LessonSlotMapper;
import com.ringle.courseregistration.domain.lesson.repository.LessonSlotRepository;
import com.ringle.courseregistration.domain.lesson.repository.TimeUnitRepository;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotCreateDto;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotDeleteDto;
import com.ringle.courseregistration.domain.tutor.entity.Tutor;
import com.ringle.courseregistration.domain.tutor.exception.TutorNotFoundException;
import com.ringle.courseregistration.domain.tutor.repository.TutorRepository;
import com.ringle.courseregistration.global.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LessonSlotService {

    private final LessonSlotRepository lessonSlotRepository;
    private final TimeUnitRepository timeUnitRepository;
    private final TutorRepository tutorRepository;
    private final LessonSlotMapper lessonSlotMapper;

    @Transactional
    public List<LessonSlotCreateResponse> save(LessonSlotCreateDto dto) {
        final Tutor tutor = getTutor(dto.memberId());

        return dto.slots().stream()
                .map(slot -> {
                    final TimeUnit timeUnit = getTimeUnit(slot.startAt());
                    validateDate(slot.date());
                    validateLessonSlot(slot.date(), timeUnit, tutor.getId());
                    final LessonSlot savedSlot
                            = lessonSlotRepository.save(lessonSlotMapper.toLessonSlot(slot, timeUnit, tutor));
                    return lessonSlotMapper.toLessonSlotCreateResponse(savedSlot);
                })
                .toList();
    }

    private Tutor getTutor(final Long memberId) {
        return tutorRepository.findByMemberId(memberId)
                .orElseThrow(TutorNotFoundException::new);
    }

    private TimeUnit getTimeUnit(final LocalTime time) {
        return timeUnitRepository.findByStartAt(time)
                .orElseThrow(TimeUnitNotFoundException::new);
    }

    private void validateDate(final LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new InvalidDateException();
        }
    }

    private void validateLessonSlot(final LocalDate date, final TimeUnit timeUnit, final Long tutorId) {
        if (lessonSlotRepository.existsByDateAndTimeUnitAndTutorId(date, timeUnit, tutorId)) {
            throw new LessonSlotAlreadyExist();
        }
    }

    @Transactional
    public void delete(final LessonSlotDeleteDto dto) {
        final Tutor tutor = getTutor(dto.memberId());
        final LessonSlot slot = lessonSlotRepository.findById(dto.lessonSlotId())
                .orElseThrow(LessonSlotNotFound::new);
        validateLessonSlot(slot, tutor);

        slot.delete();
    }

    private void validateLessonSlot(final LessonSlot slot, final Tutor tutor) {
        if (!slot.isSameTutor(tutor)) {
            throw new ForbiddenException();
        }
    }
}
