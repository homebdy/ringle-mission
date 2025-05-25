package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.constant.LessonConstant;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.ScheduledLessonFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TutorResponse;
import com.ringle.courseregistration.domain.lesson.entity.LessonInterval;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.ScheduledLesson;
import com.ringle.courseregistration.domain.lesson.exception.InvalidDateException;
import com.ringle.courseregistration.domain.lesson.exception.LessonSlotNotFoundException;
import com.ringle.courseregistration.domain.lesson.repository.LessonSlotRepository;
import com.ringle.courseregistration.domain.lesson.repository.ScheduledLessonRepository;
import com.ringle.courseregistration.domain.lesson.service.dto.ScheduleLessonCreateCommand;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.entity.Role;
import com.ringle.courseregistration.domain.member.exception.MemberNotFoundException;
import com.ringle.courseregistration.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ScheduledLessonService {

    private final ScheduledLessonRepository scheduledLessonRepository;
    private final LessonSlotRepository lessonSlotRepository;
    private final MemberRepository memberRepository;
    private final Clock clock;

    @Transactional
    public void createScheduledLesson(final ScheduleLessonCreateCommand dto) {
        validateStudent(dto.memberId());
        validateDate(dto.startAt());
        final List<LessonSlot> slots = findAvailableSlots(dto);
        validateLessonSlotCount(slots, dto.lessonInterval());

        reserveLesson(slots, dto.memberId());
    }

    private void validateStudent(final Long memberId) {
        if (!memberRepository.existsByIdAndRole(memberId, Role.STUDENT)) {
            throw new MemberNotFoundException();
        }
    }

    private void validateDate(final LocalDateTime startAt) {
        if (startAt.isBefore(LocalDateTime.now(clock))) {
            throw new InvalidDateException();
        }
    }

    private List<LessonSlot> findAvailableSlots(final ScheduleLessonCreateCommand dto) {
        final LocalDateTime startAt = dto.startAt();
        final LocalDateTime endAt
                = startAt.plusMinutes((long) dto.lessonInterval().getLessonLength() - LessonConstant.LESSON_LENGTH);

        return lessonSlotRepository.findAllByTutorIdAndReservedAndStartAtIsBetween(dto.tutorId(), false, startAt, endAt);
    }

    private void validateLessonSlotCount(final List<LessonSlot> slots, final LessonInterval lessonInterval) {
        final int neededLessonSlotCount = lessonInterval.getLessonLength() / LessonConstant.LESSON_LENGTH;
        if (slots.size() < neededLessonSlotCount) {
            throw new LessonSlotNotFoundException();
        }
    }

    private void reserveLesson(final List<LessonSlot> slots, final Long studentId) {
        final Member student = memberRepository.getReferenceById(studentId);
        final List<ScheduledLesson> lessons = slots.stream()
                .map(slot -> {
                    slot.reserve();
                    return new ScheduledLesson(slot, student);
                })
                .toList();
        scheduledLessonRepository.saveAll(lessons);
    }

    @Transactional
    public Collection<ScheduledLessonFindResponse> findByMemberId(final Long studentId) {
        validateStudent(studentId);

        return scheduledLessonRepository.findByStudentId(studentId).stream()
                .map(this::toScheduledLessonFindResponse)
                .toList();
    }

    private ScheduledLessonFindResponse toScheduledLessonFindResponse(ScheduledLesson lesson) {
        return new ScheduledLessonFindResponse(
                lesson.getId(),
                lesson.getLessonSlot().getStartAt(),
                new TutorResponse(lesson.getTutor().getId(), lesson.getTutor().getName())
        );
    }
}
