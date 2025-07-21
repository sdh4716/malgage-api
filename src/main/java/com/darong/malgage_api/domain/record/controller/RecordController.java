// 경로: com.darong.malgage_api.domain.record.service
package com.darong.malgage_api.domain.record.service;

import com.darong.malgage_api.domain.record.Record;
import com.darong.malgage_api.domain.record.dto.RecordRequestDto;
import com.darong.malgage_api.domain.record.dto.RecordResponseDto;
import com.darong.malgage_api.domain.record.repository.RecordRepository;
import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.emotion.Emotion;
import com.darong.malgage_api.domain.record.repository.CategoryRepository;
import com.darong.malgage_api.domain.record.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordService {

    private final RecordRepository recordRepository;
    private final CategoryRepository categoryRepository;
    private final EmotionRepository emotionRepository;

    public RecordResponseDto create(RecordRequestDto request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Emotion emotion = emotionRepository.findById(request.getEmotionId())
                .orElseThrow(() -> new IllegalArgumentException("Emotion not found"));

        Record record = Record.create(
                request.getAmount(),
                request.getType(),
                request.getDate(),
                category,
                emotion,
                request.getPaymentMethod(),
                request.isInstallment(),
                request.getMemo(),
                request.getUser() // User는 컨트롤러나 서비스에서 주입해야 함
        );

        recordRepository.save(record);
        return RecordResponseDto.from(record);
    }

    public RecordResponseDto get(Long id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));
        return RecordResponseDto.from(record);
    }

    public RecordResponseDto update(Long id, RecordRequestDto request) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Emotion emotion = emotionRepository.findById(request.getEmotionId())
                .orElseThrow(() -> new IllegalArgumentException("Emotion not found"));

        record.update(
                request.getAmount(),
                request.getType(),
                request.getDate(),
                category,
                emotion,
                request.getPaymentMethod(),
                request.isInstallment(),
                request.getMemo()
        );

        return RecordResponseDto.from(record);
    }

    public void delete(Long id) {
        recordRepository.deleteById(id);
    }

    public List<RecordResponseDto> getAll() {
        return recordRepository.findAll()
                .stream()
                .map(RecordResponseDto::from)
                .collect(Collectors.toList());
    }
}
