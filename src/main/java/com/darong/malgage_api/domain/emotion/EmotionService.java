package com.darong.malgage_api.domain.emotion;

import com.darong.malgage_api.domain.emotion.dto.EmotionResponseDto;
import com.darong.malgage_api.domain.emotion.repository.EmotionDefaultRepository;
import com.darong.malgage_api.domain.emotion.repository.UserEmotionRepository;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionDefaultRepository emotionDefaultRepository;
    private final UserEmotionRepository userEmotionRepository;

    public List<EmotionResponseDto> getEmotionsForUser(User user) {
        List<EmotionResponseDto> defaults = emotionDefaultRepository.findAll()
                .stream()
                .map(EmotionResponseDto::from)
                .toList();

        List<EmotionResponseDto> customs = userEmotionRepository.findByUserAndEnabledTrue(user)
                .stream()
                .map(EmotionResponseDto::from)
                .toList();

        return Stream.concat(defaults.stream(), customs.stream())
                .sorted(Comparator.comparing(EmotionResponseDto::getName))
                .toList();
    }
}
