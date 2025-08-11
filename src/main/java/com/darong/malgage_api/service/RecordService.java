// domain/record/RecordService.java
package com.darong.malgage_api.service;

import com.darong.malgage_api.controller.dto.request.record.RecordSaveRequestDto;
import com.darong.malgage_api.controller.dto.response.record.RecordResponseDto;
import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.record.InstallmentSchedule;
import com.darong.malgage_api.domain.record.Record;
import com.darong.malgage_api.global.exception.NotFoundException;
import com.darong.malgage_api.repository.category.CategoryRepository;
import com.darong.malgage_api.repository.emotion.EmotionRepository;
import com.darong.malgage_api.repository.record.InstallmentScheduleQueryRepository;
import com.darong.malgage_api.repository.record.InstallmentScheduleRepository;
import com.darong.malgage_api.repository.record.RecordQueryRepository;
import com.darong.malgage_api.repository.record.RecordRepository;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final RecordQueryRepository recordQueryRepository;
    private final CategoryRepository categoryRepository;
    private final EmotionRepository emotionRepository;
    private final InstallmentScheduleRepository installmentScheduleRepository;
    private final InstallmentScheduleQueryRepository installmentScheduleQueryRepository;

    @Transactional
    public void createRecord(User user, RecordSaveRequestDto dto) {

        // OSIV false 환경 → 직접 엔티티를 영속화해서 조회
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("카테고리를 찾을 수 없습니다."));

        Emotion emotion = emotionRepository.findById(dto.getEmotionId())
                .orElseThrow(() -> new NotFoundException("감정을 찾을 수 없습니다."));

        Record record = Record.create(
                dto.getAmount(),
                dto.getType(),
                dto.getDate(),
                category,
                emotion,
                dto.getPaymentMethod(),
                dto.isInstallment(),
                dto.getInstallmentMonths(),
                dto.getMemo(),
                user
        );

        recordRepository.save(record);

        if (dto.isInstallment()) {
            List<InstallmentSchedule> schedules = createInstallmentSchedules(record);
            installmentScheduleRepository.saveAll(schedules);
        }
    }



    /**
     * 월별 가계부 기록 조회
     */
    public List<RecordResponseDto> getMonthlyRecords(User user, int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        // 일반 기록 조회
        List<Record> normalRecords = recordQueryRepository.findRecordsByUserAndDateBetween(user, start, end);
        List<RecordResponseDto> normalDtos = normalRecords.stream()
                .map(RecordResponseDto::from)
                .toList();

        // 할부 회차 기록 조회 (InstallmentSchedule 기준으로!)
        List<InstallmentSchedule> schedules = installmentScheduleQueryRepository
                .findByUserAndScheduledDateBetween(user, start, end);
        List<RecordResponseDto> installmentDtos = schedules.stream()
                .map(RecordResponseDto::fromInstallment)
                .toList();

        // 두 결과 합침 후 정렬
        List<RecordResponseDto> merged = new ArrayList<>();
        merged.addAll(normalDtos);
        merged.addAll(installmentDtos);

        // 날짜 내림차순 정렬
        merged.sort(Comparator.comparing(RecordResponseDto::getDate).reversed());

        return merged;
    }





    public List<InstallmentSchedule> createInstallmentSchedules(Record record) {
        List<InstallmentSchedule> result = new ArrayList<>();
        int monthlyAmount = record.getAmount() / record.getInstallmentMonths();

        for (int i = 0; i < record.getInstallmentMonths(); i++) {
            LocalDateTime scheduledDate = calculateInstallmentDate(record.getDate(), i);
            result.add(InstallmentSchedule.create(record, i + 1, scheduledDate, monthlyAmount));
        }

        return result;
    }

    private LocalDateTime calculateInstallmentDate(LocalDateTime baseDateTime, int monthOffset) {
        LocalDate baseDate = baseDateTime.toLocalDate();
        LocalDate targetDate = baseDate.plusMonths(monthOffset);

        int dayOfMonth = baseDate.getDayOfMonth();
        int lastDayOfMonth = targetDate.lengthOfMonth();

        // 실제 존재하는 날짜로 보정
        LocalDate correctedDate = targetDate.withDayOfMonth(Math.min(dayOfMonth, lastDayOfMonth));

        return correctedDate.atTime(baseDateTime.toLocalTime());  // 시간도 유지
    }




}