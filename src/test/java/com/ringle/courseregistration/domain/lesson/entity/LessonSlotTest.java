package com.ringle.courseregistration.domain.lesson.entity;

import com.ringle.courseregistration.domain.lesson.exception.InvalidDateException;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.entity.Role;
import com.ringle.courseregistration.global.exception.ForbiddenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("LessonSlot 도메인 단위 테스트")
class LessonSlotTest {

    private final Clock clock = Clock.systemUTC();

    private final Member member = new Member("튜터", Role.TUTOR);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(member, "id", 1L);
    }

    @Nested
    @DisplayName("Member 객체 생성")
    class CreateMember {

        @Test
        @DisplayName("올바른 시간일 경우 정상적으로 LessonSlot을 생성한다")
        void shouldCreateLessonSlot() {
            // given
            LocalDateTime startAt = LocalDate.now(clock).plusDays(1).atStartOfDay();

            // when
            LessonSlot slot = LessonSlot.of(startAt, member, clock);

            // then
            assertThat(slot).isNotNull();
        }

        @Test
        @DisplayName("과거 시간이면 InvalidDateException을 던진다")
        void shouldThrowExceptionForPastTime() {
            // given
            LocalDateTime pastTime = LocalDate.now(clock).minusDays(1).atStartOfDay();

            // when, then
            assertThatThrownBy(() -> LessonSlot.of(pastTime, member, clock))
                    .isInstanceOf(InvalidDateException.class);
        }

        @Test
        @DisplayName("시작 시간이 30분 단위가 아니면 InvalidDateException을 던진다")
        void shouldThrowExceptionIfNotDivisibleBy30() {
            // given
            LocalDateTime invalidTime = LocalDate.now(clock).atStartOfDay().plusMinutes(2);

            // when, then
            assertThatThrownBy(() -> LessonSlot.of(invalidTime, member, clock))
                    .isInstanceOf(InvalidDateException.class);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class DeleteMethod {

        @Test
        @DisplayName("튜터 본인이면 삭제가 성공하고 deleteAt이 설정된다")
        void shouldDeleteSuccessfully() {
            // given
            LocalDateTime startAt = LocalDate.now(clock).plusDays(1).atStartOfDay();
            LessonSlot slot = LessonSlot.of(startAt, member, clock);

            // when
            slot.delete(member.getId(), clock);

            // then
            assertThat(slot.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("튜터가 아닌 사용자가 삭제하면 ForbiddenException이 발생한다")
        void shouldThrowForbiddenExceptionWhenInvalidUser() {
            // given
            LocalDateTime startAt = LocalDate.now(clock).plusDays(1).atStartOfDay();
            LessonSlot slot = LessonSlot.of(startAt, member, clock);
            Long anotherUserId = 999L;

            // expect
            assertThatThrownBy(() -> slot.delete(anotherUserId, clock))
                    .isInstanceOf(ForbiddenException.class);
        }
    }

    @Nested
    @DisplayName("reserve 메서드")
    class ReserveMethod {

        @Test
        @DisplayName("호출 시 reserved가 true로 변경된다")
        void shouldMarkAsReserved() {
            // given
            LocalDateTime startAt = LocalDate.now(clock).plusDays(1).atStartOfDay();
            LessonSlot slot = LessonSlot.of(startAt, member, clock);

            // when
            slot.reserve();

            // then
            assertThat(slot.isReserved()).isTrue();
        }
    }
}
