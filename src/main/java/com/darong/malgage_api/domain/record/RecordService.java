package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.domain.record.dto.RecordRequestDto;
import com.darong.malgage_api.domain.record.dto.RecordResponseDto;
import com.darong.malgage_api.domain.category.UserCategory;
import com.darong.malgage_api.domain.category.repository.UserCategoryRepository;
import com.darong.malgage_api.domain.emotion.UserEmotion;
import com.darong.malgage_api.domain.emotion.repository.UserEmotionRepository;
import com.darong.malgage_api.domain.user.User;
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
    private final UserCategoryRepository userCategoryRepository;
    private final UserEmotionRepository userEmotionRepository;

    public RecordResponseDto create(RecordRequestDto request, User user) {
        UserCategory category = userCategoryRepository.findById(request.getCategoryId())
                .filter(c -> c.getUser().equals(user) && c.isEnabled())
                .orElseThrow(() -> new IllegalArgumentException("사용자 카테고리를 찾을 수 없습니다."));

        UserEmotion emotion = userEmotionRepository.findById(request.getEmotionId())
                .filter(e -> e.getUser().equals(user) && e.isEnabled())
                .orElseThrow(() -> new IllegalArgumentException("사용자 감정을 찾을 수 없습니다."));

        Record record = Record.create(
                request.getAmount(),
                request.getType(),
                request.getDate(),
                category,
                emotion,
                request.getPaymentMethod(),
                request.isInstallment(),
                request.getInstallmentMonth(),
                request.getMemo(),
                user
        );

        recordRepository.save(record);
        return RecordResponseDto.from(record);
    }

    public RecordResponseDto get(Long id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));
        return RecordResponseDto.from(record);
    }

    public RecordResponseDto update(Long id, RecordRequestDto request, User user) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));

        UserCategory category = userCategoryRepository.findById(request.getCategoryId())
                .filter(c -> c.getUser().equals(user) && c.isEnabled())
                .orElseThrow(() -> new IllegalArgumentException("사용자 카테고리를 찾을 수 없습니다."));

        UserEmotion emotion = userEmotionRepository.findById(request.getEmotionId())
                .filter(e -> e.getUser().equals(user) && e.isEnabled())
                .orElseThrow(() -> new IllegalArgumentException("사용자 감정을 찾을 수 없습니다."));

        record.update(
                request.getAmount(),
                request.getType(),
                request.getDate(),
                category,
                emotion,
                request.getPaymentMethod(),
                request.isInstallment(),
                request.getInstallmentMonth(),
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
