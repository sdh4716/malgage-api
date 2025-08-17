package com.darong.malgage_api.controller;

import com.darong.malgage_api.global.security.CurrentUser;
import com.darong.malgage_api.controller.dto.response.statistics.StatisticsResponseDto;
import com.darong.malgage_api.domain.statistics.StatisticsPeriod;
import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<StatisticsResponseDto> getStatistics(
            @CurrentUser User user,
            @RequestParam("type") String type,
            @RequestParam("year") int year,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        StatisticsPeriod period = StatisticsPeriod.from(type);
        return ResponseEntity.ok(
                statisticsService.getStatistics(user, period, year, month)
        );
    }
}
