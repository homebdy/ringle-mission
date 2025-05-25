package com.ringle.courseregistration.domain.lesson.entity;

import com.ringle.courseregistration.domain.lesson.constant.LessonConstant;
import com.ringle.courseregistration.domain.lesson.exception.InvalidDateException;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.global.exception.ForbiddenException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Clock;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE lesson_slot SET delete_at = true WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class LessonSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member tutor;

    private boolean reserved = false;

    private LocalDateTime deletedAt;

    @Version
    private Long version;

    private LessonSlot(LocalDateTime startAt, Member tutor) {
        this.startAt = startAt;
        this.tutor = tutor;
    }

    public static LessonSlot of(LocalDateTime startAt, Member tutor, Clock clock) {
        validateStartAt(startAt, clock);
        return new LessonSlot(startAt, tutor);
    }

    private static void validateStartAt(LocalDateTime startAt, Clock clock) {
        LocalDateTime now = LocalDateTime.now(clock);
        if (startAt.isBefore(now) || startAt.getMinute() % LessonConstant.LESSON_LENGTH != 0) {
            throw new InvalidDateException();
        }
    }

    public void delete(Long memberId, Clock clock) {
        if (!tutor.getId().equals(memberId)) {
            throw new ForbiddenException();
        }
        this.deletedAt = LocalDateTime.now(clock);
    }

    public void reserve() {
        this.reserved = true;
    }
}
