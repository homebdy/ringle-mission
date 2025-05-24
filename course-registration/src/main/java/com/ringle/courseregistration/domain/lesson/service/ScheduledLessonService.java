package com.ringle.courseregistration.domain.lesson.service;

import com.ringle.courseregistration.domain.lesson.constant.LessonConstant;
import com.ringle.courseregistration.domain.lesson.entity.LessonSlot;
import com.ringle.courseregistration.domain.lesson.entity.ScheduledLesson;
import com.ringle.courseregistration.domain.lesson.entity.TimeUnit;
import com.ringle.courseregistration.domain.lesson.exception.AlreadyRegisterLesson;
import com.ringle.courseregistration.domain.lesson.exception.LessonSlotNotFoundException;
import com.ringle.courseregistration.domain.lesson.mapper.ScheduledLessonMapper;
import com.ringle.courseregistration.domain.lesson.repository.LessonSlotRepository;
import com.ringle.courseregistration.domain.lesson.repository.ScheduledLessonRepository;
import com.ringle.courseregistration.domain.lesson.repository.TimeUnitRepository;
import com.ringle.courseregistration.domain.lesson.service.dto.ScheduleLessonCreateDto;
import com.ringle.courseregistration.domain.student.entity.Student;
import com.ringle.courseregistration.domain.student.exception.StudentNotFoundException;
import com.ringle.courseregistration.domain.student.repository.StudentRepository;
import com.ringle.courseregistration.domain.tutor.entity.Tutor;
import com.ringle.courseregistration.domain.tutor.exception.TutorNotFoundException;
import com.ringle.courseregistration.domain.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class ScheduledLessonService {

    private final ScheduledLessonRepository scheduledLessonRepository;
    private final LessonSlotRepository lessonSlotRepository;
    private final TutorRepository tutorRepository;
    private final StudentRepository studentRepository;
    private final TimeUnitRepository timeUnitRepository;
    private final ScheduledLessonMapper scheduledLessonMapper;

    @Transactional
    public void createScheduledLesson(ScheduleLessonCreateDto dto) {
        Tutor tutor = findTutor(dto.tutorId());
        Student student = findStudent(dto.memberId());
        List<LessonSlot> slots = findAvailableSlots(dto, tutor);
        
        validateSlotsAvailability(slots);
        registerScheduledLessons(slots, student);
    }

    private Tutor findTutor(Long tutorId) {
        return tutorRepository.findByMemberId(tutorId)
                .orElseThrow(TutorNotFoundException::new);
    }

    private Student findStudent(Long studentId) {
        return studentRepository.findByMemberId(studentId)
                .orElseThrow(StudentNotFoundException::new);
    }

    private List<LessonSlot> findAvailableSlots(ScheduleLessonCreateDto dto, Tutor tutor) {
        return IntStream.range(0, dto.duration().getLessonLength() / LessonConstant.LESSON_LENGTH)
                .mapToObj(i -> {
                    TimeUnit timeUnit = findTimeUnit(dto.startAt().plusMinutes((long) i * LessonConstant.LESSON_LENGTH));
                    return findLessonSlot(dto.date(), timeUnit, tutor.getId());
                })
                .toList();
    }

    private TimeUnit findTimeUnit(LocalTime startAt) {
        return timeUnitRepository.findByStartAt(startAt)
                .orElseThrow(LessonSlotNotFoundException::new);
    }

    private LessonSlot findLessonSlot(LocalDate date, TimeUnit timeUnit, Long tutorId) {
        return lessonSlotRepository.findByDateAndTimeUnitAndTutorId(date, timeUnit, tutorId)
                .orElseThrow(LessonSlotNotFoundException::new);
    }

    private void validateSlotsAvailability(List<LessonSlot> slots) {
        if (slots.stream().anyMatch(LessonSlot::isRegistered)) {
            throw new AlreadyRegisterLesson();
        }
    }

    private void registerScheduledLessons(List<LessonSlot> slots, Student student) {
        slots.forEach(slot -> {
            slot.register();
            ScheduledLesson scheduledLesson = scheduledLessonMapper.toScheduledLesson(slot, student);
            scheduledLessonRepository.save(scheduledLesson);
        });
    }
}
