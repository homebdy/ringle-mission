package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.constant.LessonConstant;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TimeUnitFindResponse;
import com.ringle.courseregistration.domain.lesson.entity.Duration;
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
import com.ringle.courseregistration.domain.lesson.service.dto.TimeUnitFindDto;
import com.ringle.courseregistration.domain.tutor.entity.Tutor;
import com.ringle.courseregistration.domain.tutor.exception.TutorNotFoundException;
import com.ringle.courseregistration.domain.tutor.repository.TutorRepository;
import com.ringle.courseregistration.global.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public TimeUnitFindResponse findTimeUnitByDateAndLessonLength(final TimeUnitFindDto dto) {
        List<LessonSlot> allSlots = lessonSlotRepository.findByDate(dto.date());
        Map<Tutor, List<LessonSlot>> slotsByTutor = allSlots.stream()
                .collect(Collectors.groupingBy(LessonSlot::getTutor));

        Set<TimeUnit> result = new HashSet<>();
        for (Tutor tutor : slotsByTutor.keySet()) {
            List<LessonSlot> sorted = slotsByTutor.get(tutor).stream()
                    .sorted(Comparator.comparing(ls -> ls.getTimeUnit().getStartAt()))
                    .toList();

            if (dto.duration().equals(Duration.MINUTES_30)) {
                result.addAll(getTimeUnitsFor30MinuteLesson(sorted));
                continue;
            }
            result.addAll(getTimeUnitsFor60MinuteLesson(sorted));
        }
        return new TimeUnitFindResponse(dto.date(), result.stream().map(TimeUnit::getStartAt).toList());
    }

    private Collection<TimeUnit> getTimeUnitsFor30MinuteLesson(List<LessonSlot> sortedSlots) {
        return sortedSlots.stream()
                .map(LessonSlot::getTimeUnit)
                .toList();
    }

    private Collection<TimeUnit> getTimeUnitsFor60MinuteLesson(List<LessonSlot> sortedSlots) {
        List<TimeUnit> result = new ArrayList<>();
        for (int i = 0; i < sortedSlots.size() - 1; i++) {
            LessonSlot current = sortedSlots.get(i);
            LessonSlot next = sortedSlots.get(i + 1);

            if (current.getTimeUnit().getStartAt().plusMinutes(LessonConstant.LESSON_LENGTH).equals(next.getTimeUnit().getStartAt())) {
                result.add(current.getTimeUnit());
            }
        }
        return result;
    }
}
