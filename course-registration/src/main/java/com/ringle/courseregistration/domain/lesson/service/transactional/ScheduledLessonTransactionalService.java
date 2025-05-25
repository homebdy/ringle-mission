package com.ringle.courseregistration.domain.lesson.service.transactional;

import com.ringle.courseregistration.domain.lesson.constant.LessonConstant;
import com.ringle.courseregistration.domain.lesson.entity.LessonInterval;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.ScheduledLesson;
import com.ringle.courseregistration.domain.lesson.exception.LessonSlotNotFoundException;
import com.ringle.courseregistration.domain.lesson.repository.LessonSlotRepository;
import com.ringle.courseregistration.domain.lesson.repository.ScheduledLessonRepository;
import com.ringle.courseregistration.domain.lesson.service.dto.ScheduleLessonCreateCommand;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ScheduledLessonTransactionalService {

    private final ScheduledLessonRepository scheduledLessonRepository;
    private final LessonSlotRepository lessonSlotRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createScheduledLessonWithTx(ScheduleLessonCreateCommand dto) {
        final List<LessonSlot> slots = findAvailableSlots(dto);
        validateLessonSlotCount(slots, dto.lessonInterval());

        reserveLesson(slots, dto.memberId());
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
}
