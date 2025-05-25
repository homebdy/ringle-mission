package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.constant.LessonConstant;
import com.ringle.courseregistration.domain.lesson.controller.dto.request.LessonSlotCreateRequest;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.LessonSlotCreateResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TimeUnitFindResponse;
import com.ringle.courseregistration.domain.lesson.controller.dto.response.TutorFindResponse;
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
import com.ringle.courseregistration.global.exception.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonSlotServiceTest {

    private final Clock clock = Clock.systemUTC();
    @Mock
    private LessonSlotRepository lessonSlotRepository;
    @Mock
    private MemberRepository memberRepository;
    private LessonSlotService lessonSlotService;

    private Member tutor;

    @BeforeEach
    void setUp() {
        lessonSlotService = new LessonSlotService(lessonSlotRepository, memberRepository, clock);
        tutor = new Member("tutor", Role.TUTOR);
        ReflectionTestUtils.setField(tutor, "id", 1L);
    }

    private LessonSlot lessonSlot(Member tutor, LocalDateTime time, Long id) {
        LessonSlot slot = LessonSlot.of(time, tutor, clock);
        ReflectionTestUtils.setField(slot, "id", id);
        return slot;
    }

    @Nested
    @DisplayName("save 메서드")
    class Save {

        @Test
        @DisplayName("유효한 튜터와 슬롯이면 저장 후 응답을 반환한다")
        void savesValidLessonSlots() {
            // given
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            LessonSlotCreateCommand command = new LessonSlotCreateCommand(tutor.getId(), List.of(new LessonSlotCreateRequest(startAt)));
            LessonSlot lessonSlot = LessonSlot.of(command.slots().get(0).startAt(), tutor, clock);
            ReflectionTestUtils.setField(lessonSlot, "id", 1L);

            when(memberRepository.existsByIdAndRole(tutor.getId(), Role.TUTOR)).thenReturn(true);
            when(memberRepository.getReferenceById(tutor.getId())).thenReturn(tutor);
            when(lessonSlotRepository.existsByStartAtAndTutorId(any(), anyLong())).thenReturn(false);
            when(lessonSlotRepository.save(any())).thenReturn(lessonSlot);

            // when
            List<LessonSlotCreateResponse> result = lessonSlotService.save(command);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("튜터 정보가 존재하지 않으면 MemberNotFoundException이 발생한다")
        void throwsWhenTutorNotFound() {
            // given
            LessonSlotCreateCommand command = new LessonSlotCreateCommand(tutor.getId(), List.of());

            when(memberRepository.existsByIdAndRole(tutor.getId(), Role.TUTOR)).thenReturn(false);

            // when, then
            assertThatThrownBy(() -> lessonSlotService.save(command))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("동일 시간 슬롯이 존재하면 LessonSlotAlreadyExistException이 발생한다")
        void throwsWhenSlotAlreadyExists() {
            // given
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            LessonSlotCreateCommand command = new LessonSlotCreateCommand(
                    tutor.getId(),
                    List.of(new LessonSlotCreateRequest(startAt))
            );

            when(memberRepository.existsByIdAndRole(tutor.getId(), Role.TUTOR)).thenReturn(true);
            when(memberRepository.getReferenceById(tutor.getId())).thenReturn(tutor);
            when(lessonSlotRepository.existsByStartAtAndTutorId(startAt, tutor.getId())).thenReturn(true);

            // when, then
            assertThatThrownBy(() -> lessonSlotService.save(command))
                    .isInstanceOf(LessonSlotAlreadyExistException.class);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("유효한 튜터와 슬롯이면 정상 삭제된다")
        void deletesSlot() {
            // given
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            LessonSlotDeleteCommand command = new LessonSlotDeleteCommand(tutor.getId(), 1L);
            LessonSlot slot = LessonSlot.of(startAt, tutor, clock);

            when(memberRepository.existsByIdAndRole(tutor.getId(), Role.TUTOR)).thenReturn(true);
            when(lessonSlotRepository.findById(anyLong())).thenReturn(Optional.of(slot));

            // when
            lessonSlotService.delete(command);

            // then
            assertThat(slot.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("슬롯이 존재하지 않으면 LessonSlotNotFoundException이 발생한다")
        void throwsWhenSlotNotFound() {
            // given
            LessonSlotDeleteCommand command = new LessonSlotDeleteCommand(tutor.getId(), 99L);

            when(memberRepository.existsByIdAndRole(tutor.getId(), Role.TUTOR)).thenReturn(true);
            when(lessonSlotRepository.findById(99L)).thenReturn(Optional.empty());

            // when, then
            assertThatThrownBy(() -> lessonSlotService.delete(command))
                    .isInstanceOf(LessonSlotNotFoundException.class);
        }

        @Test
        @DisplayName("슬롯이 존재하지 않으면 LessonSlotNotFoundException이 발생한다")
        void throwsWhenDifferentMember() {
            // given
            LessonSlotDeleteCommand command = new LessonSlotDeleteCommand(tutor.getId(), 1L);
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            Member another = new Member("another", Role.TUTOR);
            ReflectionTestUtils.setField(another, "id", 22L);

            when(memberRepository.existsByIdAndRole(tutor.getId(), Role.TUTOR)).thenReturn(true);
            when(lessonSlotRepository.findById(any())).thenReturn(Optional.of(LessonSlot.of(startAt, another, clock)));

            // when, then
            assertThatThrownBy(() -> lessonSlotService.delete(command))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Nested
    @DisplayName("findTimeUnitByDateAndLessonLength 메서드")
    class FindTimeUnit {

        @Test
        @DisplayName("60분 수업에 대한 연속 수업 가능 시작 시간을 반환한다")
        void returnsAvailable60TimeUnits() {
            // given
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            List<LessonSlot> slots = List.of(
                    lessonSlot(tutor, startAt, 1L),
                    lessonSlot(tutor, startAt.plusMinutes(LessonConstant.LESSON_LENGTH), 2L)
            );
            when(lessonSlotRepository.findAllByDate(false, startAt.toLocalDate())).thenReturn(slots);

            AvailableTimeFindCommand command = new AvailableTimeFindCommand(startAt.toLocalDate(), LessonInterval.MINUTES_60);

            // when
            TimeUnitFindResponse response = lessonSlotService.findTimeUnitByDateAndLessonLength(command);

            // then
            assertThat(response.availableTimeUnits()).hasSize(1);
            assertThat(response.availableTimeUnits()).contains(startAt);
        }

        @Test
        @DisplayName("30분 수업에 대한 연속 수업 가능 시작 시간을 반환한다")
        void returnsAvailable30TimeUnits() {
            // given
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            AvailableTimeFindCommand command = new AvailableTimeFindCommand(startAt.toLocalDate(), LessonInterval.MINUTES_30);
            List<LessonSlot> slots = List.of(
                    lessonSlot(tutor, startAt, 1L),
                    lessonSlot(tutor, startAt.plusMinutes(LessonConstant.LESSON_LENGTH), 2L)
            );

            when(lessonSlotRepository.findAllByDate(false, startAt.toLocalDate())).thenReturn(slots);

            // when
            TimeUnitFindResponse response = lessonSlotService.findTimeUnitByDateAndLessonLength(command);

            // then
            assertThat(response.availableTimeUnits()).contains(startAt);
            assertThat(response.availableTimeUnits()).contains(startAt.plusMinutes(LessonConstant.LESSON_LENGTH));
            assertThat(response.availableTimeUnits()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findTutorByTimeAndLessonInterval 메서드")
    class FindTutor {

        @Test
        @DisplayName("60분대 수업 가능 시간대를 가진 튜터 목록을 반환한다")
        void returnsAvailableTutorsFor60Minutes() {
            // given
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            LessonInterval interval = LessonInterval.MINUTES_60;
            Member another = new Member("another", Role.TUTOR);
            ReflectionTestUtils.setField(another, "id", 2L);

            List<LessonSlot> slots = List.of(
                    lessonSlot(tutor, startAt, 1L),
                    lessonSlot(tutor, startAt.plusMinutes(LessonConstant.LESSON_LENGTH), 2L),
                    lessonSlot(another, startAt, 3L)
            );
            when(lessonSlotRepository.findAllByReservedAndStartAtIsBetween(eq(false), any(), any()))
                    .thenReturn(slots);

            TutorFindCommand command = new TutorFindCommand(startAt.plusMinutes(30), interval);

            // when
            TutorFindResponse response = lessonSlotService.findTutorByTimeAndLessonInterval(command);

            // then
            assertThat(response.timeUnitResponses()).hasSize(1);
        }

        @Test
        @DisplayName("30분대 수업 가능 시간대를 가진 튜터 목록을 반환한다")
        void returnsAvailableTutorsFor30Minutes() {
            // given
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            LessonInterval interval = LessonInterval.MINUTES_30;
            Member another = new Member("another", Role.TUTOR);
            ReflectionTestUtils.setField(another, "id", 2L);

            List<LessonSlot> slots = List.of(
                    lessonSlot(tutor, startAt, 1L),
                    lessonSlot(tutor, startAt.plusMinutes(30), 2L),
                    lessonSlot(another, startAt, 3L)
            );
            when(lessonSlotRepository.findAllByReservedAndStartAtIsBetween(eq(false), any(), any()))
                    .thenReturn(slots);

            TutorFindCommand command = new TutorFindCommand(startAt, interval);

            // when
            TutorFindResponse response = lessonSlotService.findTutorByTimeAndLessonInterval(command);

            // then
            assertThat(response.timeUnitResponses()).hasSize(2);
        }
    }
}
