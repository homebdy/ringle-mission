package com.ringle.courseregistration.integration;

import com.ringle.courseregistration.domain.lesson.entity.LessonInterval;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.ScheduledLesson;
import com.ringle.courseregistration.domain.lesson.repository.LessonSlotRepository;
import com.ringle.courseregistration.domain.lesson.repository.ScheduledLessonRepository;
import com.ringle.courseregistration.domain.lesson.service.ScheduledLessonService;
import com.ringle.courseregistration.domain.lesson.service.dto.ScheduleLessonCreateCommand;
import com.ringle.courseregistration.domain.member.entity.Member;
import com.ringle.courseregistration.domain.member.entity.Role;
import com.ringle.courseregistration.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ScheduledLessonServiceConcurrencyTest {

    @Autowired
    private ScheduledLessonService scheduledLessonService;

    @Autowired
    private LessonSlotRepository lessonSlotRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ScheduledLessonRepository scheduledLessonRepository;

    private Member tutor;
    private Member student;
    private Clock fixedClock;
    private LocalDateTime startAt;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.systemUTC();
        startAt = LocalDate.now(fixedClock).plusDays(1).atStartOfDay();

        tutor = memberRepository.save(new Member("튜터", Role.TUTOR));
        student = memberRepository.save(new Member("학생", Role.STUDENT));

        lessonSlotRepository.save(LessonSlot.of(startAt, tutor, fixedClock));
    }

    @Test
    @DisplayName("동시에 여러명 예약 요청시 중복 예약이 발생하지 않는다")
    void createLessonNoDuplicate() throws InterruptedException {
        int threadCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<Throwable>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                try {
                    scheduledLessonService.createScheduledLesson(
                            new ScheduleLessonCreateCommand(
                                    student.getId(),
                                    startAt,
                                    LessonInterval.MINUTES_30,
                                    tutor.getId()
                            )
                    );
                    return null;
                } catch (Throwable e) {
                    return e;
                } finally {
                    latch.countDown();
                }
            }));
        }

        latch.await();

        List<ScheduledLesson> lessons = scheduledLessonRepository.findAll();
        long reservedCount = lessons.size();
        assertThat(reservedCount).isEqualTo(1);
    }
}