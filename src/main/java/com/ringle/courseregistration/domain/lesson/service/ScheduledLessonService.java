package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.controller.dto.response.ScheduledLessonFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TutorResponse;
import com.ringle.courseregistration.domain.lesson.entity.ScheduledLesson;
import com.ringle.courseregistration.domain.lesson.exception.AlreadyReservedLessonException;
import com.ringle.courseregistration.domain.lesson.exception.InvalidDateException;
import com.ringle.courseregistration.domain.lesson.repository.ScheduledLessonRepository;
import com.ringle.courseregistration.domain.lesson.service.dto.ScheduleLessonCreateCommand;
import com.ringle.courseregistration.domain.lesson.service.transactional.ScheduledLessonTransactionalService;
import com.ringle.courseregistration.domain.member.entity.Role;
import com.ringle.courseregistration.domain.member.exception.MemberNotFoundException;
import com.ringle.courseregistration.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class ScheduledLessonService {

    private final ScheduledLessonRepository scheduledLessonRepository;
    private final MemberRepository memberRepository;
    private final Clock clock;
    private final ScheduledLessonTransactionalService scheduledLessonTransactionalService;

    public void createScheduledLesson(final ScheduleLessonCreateCommand dto) {
        try {
            validateStudent(dto.memberId());
            validateDate(dto.startAt());

            scheduledLessonTransactionalService.createScheduledLessonWithTx(dto);
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new AlreadyReservedLessonException();
        }
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

    @Transactional(readOnly = true)
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
