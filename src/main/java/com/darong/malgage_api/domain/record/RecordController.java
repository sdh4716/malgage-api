// domain/record/RecordController.java
package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.auth.CurrentUser;
import com.darong.malgage_api.domain.record.dto.RecordRequestDto;
import com.darong.malgage_api.domain.record.dto.RecordResponseDto;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @CurrentUser User user,  // 🎉 깔끔한 코드!
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<RecordResponseDto> responses = recordService.getMonthlyRecords(user, year, month);
        return ResponseEntity.ok(responses);
    }

}