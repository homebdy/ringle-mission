package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.controller.dto.response.ScheduledLessonFindResponse;
import com.ringle.courseregistration.domain.lesson.entity.LessonInterval;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.ScheduledLesson;
import com.ringle.courseregistration.domain.lesson.repository.LessonSlotRepository;
import com.ringle.courseregistration.domain.lesson.repository.ScheduledLessonRepository;
import com.ringle.courseregistration.domain.lesson.service.dto.ScheduleLessonCreateCommand;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.entity.Role;
import com.ringle.courseregistration.domain.member.exception.MemberNotFoundException;
import com.ringle.courseregistration.domain.member.repository.MemberRepository;
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
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledLessonServiceTest {

    @Mock
    private ScheduledLessonRepository scheduledLessonRepository;

    @Mock
    private LessonSlotRepository lessonSlotRepository;

    @Mock
    private MemberRepository memberRepository;

    private Clock clock;

    private ScheduledLessonService scheduledLessonService;

    private Member student;
    private Member tutor;

    @BeforeEach
    void setUp() {
        clock = Clock.systemUTC();
        scheduledLessonService = new ScheduledLessonService(scheduledLessonRepository, lessonSlotRepository, memberRepository, clock);

        student = new Member("st", Role.STUDENT);
        tutor = new Member("tutor", Role.TUTOR);
        ReflectionTestUtils.setField(student, "id", 1L);
        ReflectionTestUtils.setField(tutor, "id", 2L);
    }

    @Nested
    @DisplayName("createScheduledLesson 메서드")
    class CreateScheduledLesson {

        @Test
        @DisplayName("학생이 맞고, 예약 가능한 슬롯이 있으면 정상 예약된다")
        void shouldCreateScheduledLessonSuccessfully() {
            // given
            Long tutorId = 2L;
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            LessonInterval interval = LessonInterval.MINUTES_60;
            ScheduleLessonCreateCommand dto = new ScheduleLessonCreateCommand(student.getId(), startAt, interval, tutorId);

            LessonSlot slot1 = lessonSlot(tutor, startAt, 1L);
            LessonSlot slot2 = lessonSlot(tutor, startAt, 2L);
            when(lessonSlotRepository.findAllByTutorIdAndReservedAndStartAtIsBetween(
                    eq(tutorId), eq(false), any(), any())).thenReturn(List.of(slot1, slot2));
            when(memberRepository.existsByIdAndRole(student.getId(), Role.STUDENT)).thenReturn(true);
            when(memberRepository.getReferenceById(student.getId())).thenReturn(student);

            // when
            scheduledLessonService.createScheduledLesson(dto);

            // then
            assertThat(slot1.isReserved()).isTrue();
            assertThat(slot2.isReserved()).isTrue();
            verify(scheduledLessonRepository).saveAll(any());
        }

        @Test
        @DisplayName("학생이 아니면 MemberNotFoundException이 발생한다")
        void shouldThrowExceptionWhenMemberIsNotStudent() {
            // given
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            ScheduleLessonCreateCommand dto
                    = new ScheduleLessonCreateCommand(tutor.getId(), startAt, LessonInterval.MINUTES_60, tutor.getId());
            when(memberRepository.existsByIdAndRole(tutor.getId(), Role.STUDENT)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> scheduledLessonService.createScheduledLesson(dto))
                    .isInstanceOf(MemberNotFoundException.class);
        }

        @Test
        @DisplayName("오늘보다 이전의 강의에 대한 신청일 경우 예외가 발생한다.")
        void shouldThrowExceptionWhenInvalidDateException() {
            // given
            LocalDateTime now = LocalDate.now(Clock.systemUTC()).atStartOfDay();
            ScheduleLessonCreateCommand dto
                    = new ScheduleLessonCreateCommand(tutor.getId(), now.minusDays(1), LessonInterval.MINUTES_60, tutor.getId());
            when(memberRepository.existsByIdAndRole(tutor.getId(), Role.STUDENT)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> scheduledLessonService.createScheduledLesson(dto))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByMemberId 메서드는")
    class FindByMemberId {

        @Test
        @DisplayName("학생 ID로 예약된 수업을 조회하여 반환한다")
        void shouldReturnScheduledLessonsForStudent() {
            // given
            LocalDateTime startAt = LocalDate.now(Clock.systemUTC()).plusDays(1).atStartOfDay();
            LessonSlot lessonSlot = lessonSlot(tutor, startAt, 1L);
            ScheduledLesson lesson = new ScheduledLesson(lessonSlot, student);

            when(scheduledLessonRepository.findByStudentId(student.getId())).thenReturn(List.of(lesson));
            when(memberRepository.existsByIdAndRole(student.getId(), Role.STUDENT)).thenReturn(true);

            // when
            Collection<ScheduledLessonFindResponse> results = scheduledLessonService.findByMemberId(student.getId());

            // then
            assertThat(results).hasSize(1);
        }

        @Test
        @DisplayName("학생이 아니면 MemberNotFoundException이 발생한다")
        void shouldThrowExceptionIfNotStudent() {
            // given
            when(memberRepository.existsByIdAndRole(1L, Role.STUDENT)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> scheduledLessonService.findByMemberId(1L))
                    .isInstanceOf(MemberNotFoundException.class);
        }
    }

    private LessonSlot lessonSlot(Member tutor, LocalDateTime time, Long id) {
        LessonSlot slot = LessonSlot.of(time, tutor, Clock.systemUTC());
        ReflectionTestUtils.setField(slot, "id", id);
        return slot;
    }
}
