// domain/record/RecordService.java
package com.darong.malgage_api.service;

import com.darong.malgage_api.controller.dto.request.record.RecordSaveRequestDto;
import com.darong.malgage_api.controller.dto.response.RecordResponseDto;
import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.record.Record;
import com.darong.malgage_api.global.exception.NotFoundException;
import com.darong.malgage_api.repository.category.CategoryRepository;
import com.darong.malgage_api.repository.emotion.EmotionRepository;
import com.darong.malgage_api.repository.record.RecordRepository;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final CategoryRepository categoryRepository;
    private final EmotionRepository emotionRepository;

    /**
     * 월별 가계부 기록 조회
     */
    public List<RecordResponseDto> getMonthlyRecords(User user, int year, int month) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);

        // LocalDate로 변환 후 lengthOfMonth() 사용
        int lastDay = startDate.toLocalDate().lengthOfMonth();
        LocalDateTime endDate = LocalDateTime.of(year, month, lastDay, 23, 59, 59);

        List<Record> records = recordRepository.findByUserAndDateBetween(user, startDate, endDate);
        return records.stream()
                .map(RecordResponseDto::from)
                .collect(Collectors.toList());
    }

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
                    dto.getInstallmentMonth(),
                    dto.getMemo(),
                    user
                );

        recordRepository.save(record);
    }



}