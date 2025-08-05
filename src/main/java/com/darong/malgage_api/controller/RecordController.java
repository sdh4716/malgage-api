// domain/record/RecordController.java
package com.darong.malgage_api.controller;

import com.darong.malgage_api.auth.CurrentUser;
import com.darong.malgage_api.controller.dto.request.record.RecordSaveRequestDto;
import com.darong.malgage_api.service.RecordService;
import com.darong.malgage_api.controller.dto.response.RecordResponseDto;
import com.darong.malgage_api.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    /**
     * ✅ 월별 가계부 기록 조회
     * 예: /api/records/by-month?year=2025&month=7
     */
    @GetMapping("/by-month")
    public ResponseEntity<List<RecordResponseDto>> getMonthlyRecords(
            @CurrentUser User user,
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<RecordResponseDto> responses = recordService.getMonthlyRecords(user, year, month);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<Void> createRecord(
            @CurrentUser User user,
            @RequestBody @Valid RecordSaveRequestDto dto
    ) {

        log.info("isInstallment = {}", String.valueOf(dto.isInstallment()));
        log.info("installmentMonth = {}", String.valueOf(dto.getInstallmentMonths()));
        recordService.createRecord(user, dto);
        return ResponseEntity.ok().build();
    }

}