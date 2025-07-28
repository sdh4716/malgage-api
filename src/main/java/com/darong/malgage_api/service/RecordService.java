// domain/record/RecordService.java
package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.repository.CategoryRepository;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.emotion.repository.EmotionRepository;
import com.darong.malgage_api.domain.record.dto.RecordRequestDto;
import com.darong.malgage_api.domain.record.dto.RecordResponseDto;
import com.darong.malgage_api.domain.record.repository.RecordRepository;
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

}