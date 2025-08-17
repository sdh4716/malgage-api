// domain/record/RecordController.java
package com.darong.malgage_api.controller;

import com.darong.malgage_api.global.security.CurrentUser;
import com.darong.malgage_api.controller.dto.request.record.RecordSaveRequestDto;
import com.darong.malgage_api.controller.dto.request.record.RecordUpdateRequestDto;
import com.darong.malgage_api.service.RecordService;
import com.darong.malgage_api.controller.dto.response.record.RecordResponseDto;
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
     * ✅ 가계부 기록 단건 조회
     */
    @GetMapping("/{recordId}")
    public ResponseEntity<RecordResponseDto> getRecordById(
            @CurrentUser User user,
            @PathVariable Long recordId
    ) {
        RecordResponseDto response = recordService.getRecordById(user, recordId);
        return ResponseEntity.ok(response);
    }

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

        recordService.createRecord(user, dto);
        return ResponseEntity.ok().build();
    }

    /**
     * ✅ 가계부 기록 수정
     * @param user 현재 로그인 된 사용자
     * @param dto 가계부 기록 수정 전용 request dto
     */
    @PatchMapping("/{recordId}")
    public ResponseEntity<RecordResponseDto> updateRecord(
            @CurrentUser User user,
            @RequestBody RecordUpdateRequestDto dto
    ) {
        RecordResponseDto updatedRecord = recordService.updateRecord(user, dto);
        return ResponseEntity.ok(updatedRecord);
    }

    /**
     * ✅ 가계부 기록 수정
     * @param user 현재 로그인 된 사용자
     * @param recordId 가계부 기록 삭제용 id
     */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @CurrentUser User user,
            @PathVariable Long recordId
    ) {
        recordService.deleteRecord(user, recordId);
        return ResponseEntity.noContent().build();
    }



}