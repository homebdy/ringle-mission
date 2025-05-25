package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.constant.LessonConstant;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TimeUnitFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TutorFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TutorTimeUnitResponse;
import com.ringle.courseregistration.domain.lesson.entity.LessonInterval;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.exception.LessonSlotAlreadyExistException;
import com.ringle.courseregistration.domain.lesson.exception.LessonSlotNotFoundException;
import com.ringle.courseregistration.domain.lesson.repository.LessonSlotRepository;
import com.ringle.courseregistration.domain.lesson.service.dto.AvailableTimeFindCommand;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotCreateCommand;
import com.ringle.courseregistration.domain.lesson.service.dto.LessonSlotDeleteCommand;
import com.ringle.courseregistration.domain.lesson.service.dto.TutorFindCommand;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.entity.Role;
import com.ringle.courseregistration.domain.member.exception.MemberNotFoundException;
import com.ringle.courseregistration.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
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
    private final MemberRepository memberRepository;
    private final Clock clock;

    @Transactional
    public List<LessonSlotCreateResponse> save(final LessonSlotCreateCommand command) {
        validateTutor(command.memberId());

        final Member member = memberRepository.getReferenceById(command.memberId());
        return command.slots().stream()
                .map(slot -> {
                    validateLessonSlot(slot.startAt(), command.memberId());
                    return lessonSlotRepository.save(LessonSlot.of(slot.startAt(), member, clock));
                }).map(this::toLessonSlotCreateResponse)
                .toList();
    }

    private void validateTutor(final Long memberId) {
        if (!memberRepository.existsByIdAndRole(memberId, Role.TUTOR)) {
            throw new MemberNotFoundException();
        }
    }

    private void validateLessonSlot(final LocalDateTime startAt, final Long tutorId) {
        if (lessonSlotRepository.existsByStartAtAndTutorId(startAt, tutorId)) {
            throw new LessonSlotAlreadyExistException();
        }
    }

    private LessonSlotCreateResponse toLessonSlotCreateResponse(final LessonSlot slot) {
        return new LessonSlotCreateResponse(
                slot.getId(),
                slot.getStartAt(),
                slot.getTutor().getId(),
                slot.isReserved()
        );
    }

    @Transactional
    public void delete(final LessonSlotDeleteCommand command) {
        validateTutor(command.memberId());

        final LessonSlot slot = lessonSlotRepository.findById(command.lessonSlotId())
                .orElseThrow(LessonSlotNotFoundException::new);

        slot.delete(command.memberId(), clock);
    }

    // 날짜, 수업 길이 기준 수강 가능 시간대 조회
    @Transactional(readOnly = true)
    public TimeUnitFindResponse findTimeUnitByDateAndLessonLength(final AvailableTimeFindCommand command) {
        final List<LessonSlot> allSlots = lessonSlotRepository.findAllByDate(false, command.date());

        final Map<Member, List<LessonSlot>> slotsByTutor = allSlots.stream()
                .collect(Collectors.groupingBy(LessonSlot::getTutor));

        final Set<LocalDateTime> result = new HashSet<>();
        for (Member tutor : slotsByTutor.keySet()) {
            final List<LessonSlot> sorted = slotsByTutor.get(tutor).stream()
                    .sorted(Comparator.comparing(LessonSlot::getStartAt))
                    .toList();

            final Collection<LocalDateTime> answer = getLessonTime(sorted, command.lessonInterval());
            if (answer.isEmpty()) {
                continue;
            }
            result.addAll(answer);
        }
        return new TimeUnitFindResponse(result);
    }

    /**
     * 주어진 슬롯 리스트에서 수업 시간(LessonInterval)에 맞춰 연속 수업을 들을 수 있는 시작 시간들을 반환한다.
     *
     * 예:
     *   - 30분 수업: 연속된 슬롯 필요 없음 (1개만 있으면 됨)
     *   - 60분 수업: 30분 슬롯 2개가 연속되어야 함
     *   - 90분 수업: 30분 슬롯 3개가 연속되어야 함
     *
     * 로직 설명:
     *   1. 전체 수업 길이를 30분 단위로 나눠서, 연속적으로 필요한 슬롯 수(continuousSlot)를 계산
     *      - 60분 수업이면 continuousSlot = 1 (연속 1개 필요)
     *      - 90분 수업이면 continuousSlot = 2 (연속 2개 필요)
     *
     *   2. 각 시작 슬롯을 기준으로, continuousSlot만큼 떨어진 슬롯의 시간이
     *      현재 시간 + 전체 수업 시간과 일치하는지 확인
     *      - 즉, 연속된 슬롯들이 존재하는지 검사
     *
     *   3. 조건에 맞으면 해당 시작 시간을 결과에 추가
     */
    private Collection<LocalDateTime> getLessonTime(final List<LessonSlot> slots, final LessonInterval lessonInterval) {
        // 전체 수업 시간에 필요한 연속 슬롯 수 계산
        final int continuousSlot = lessonInterval.getLessonLength() / LessonConstant.LESSON_LENGTH - 1;

        final List<LocalDateTime> results = new ArrayList<>();

        for (int i = 0; i < slots.size() - continuousSlot; i++) {
            final LessonSlot current = slots.get(i);
            final LessonSlot last = slots.get(i + continuousSlot);

            // 시작 슬롯에서 전체 수업 시간만큼 더한 시간이 마지막 슬롯 시작 시간과 같아야 연속 수업이 가능
            if (current.getStartAt().plusMinutes((long) LessonConstant.LESSON_LENGTH * continuousSlot).equals(last.getStartAt())) {
                results.add(current.getStartAt());
            }
        }

        return results;
    }

    @Transactional(readOnly = true)
    public TutorFindResponse findTutorByTimeAndLessonInterval(final TutorFindCommand command) {
        final LocalDateTime startAt = command.startAt().minusMinutes(command.lessonInterval().getLessonLength());
        final LocalDateTime endAt = command.startAt().plusMinutes(command.lessonInterval().getLessonLength());

        final List<LessonSlot> allSlots
                = lessonSlotRepository.findAllByReservedAndStartAtIsBetween(false, startAt, endAt);
        final Map<Member, List<LessonSlot>> slotsByTutor = allSlots.stream()
                .collect(Collectors.groupingBy(LessonSlot::getTutor));

        final List<TutorTimeUnitResponse> timeUnitResponses = slotsByTutor.entrySet().stream()
                .map(entry -> createTutorTimeUnitResponse(entry.getKey(), entry.getValue(), command))
                .filter(res -> !res.times().isEmpty())
                .toList();

        return new TutorFindResponse(timeUnitResponses);
    }

    private TutorTimeUnitResponse createTutorTimeUnitResponse(final Member tutor, final List<LessonSlot> slots, final TutorFindCommand command) {
        final Collection<LocalDateTime> availableTimes = getLessonTime(slots, command.lessonInterval());

        return new TutorTimeUnitResponse(
                tutor.getId(),
                tutor.getName(),
                availableTimes
        );
    }
}
