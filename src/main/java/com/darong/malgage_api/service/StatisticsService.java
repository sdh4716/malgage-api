package com.darong.malgage_api.service;

import com.darong.malgage_api.controller.dto.response.statistics.StatisticsResponseDto;
import com.darong.malgage_api.domain.statistics.StatisticsPeriod;
import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.repository.statistics.StatisticsQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final StatisticsQueryRepository statisticsQueryRepository;

    public StatisticsResponseDto getStatistics(User user, StatisticsPeriod period, int year, Integer month) {
        if (period == StatisticsPeriod.MONTHLY && month == null) {
            throw new IllegalArgumentException("월별 통계는 month 파라미터가 필수입니다.");
        }
        return statisticsQueryRepository.getStatistics(user, period, year, month);
    }
}
