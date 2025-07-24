// 경로: com.darong.malgage_api.domain.record.controller
package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.domain.record.dto.RecordRequestDto;
import com.darong.malgage_api.domain.record.dto.RecordResponseDto;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    // ✅ 레코드 등록
    @PostMapping
    public ResponseEntity<RecordResponseDto> createRecord(
            @AuthenticationPrincipal User user,
            @RequestBody RecordRequestDto request
    ) {
        RecordResponseDto response = recordService.create(request, user);
        return ResponseEntity.ok(response);
    }

    // ✅ 월별 조회 (예: /api/records/by-month?year=2025&month=7)
    @GetMapping("/by-month")
    public ResponseEntity<List<RecordResponseDto>> getMonthlyRecords(
            @AuthenticationPrincipal User user,
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<RecordResponseDto> responses = recordService.getMonthlyRecords(user, year, month);
        return ResponseEntity.ok(responses);
    }

    // ✅ 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<RecordResponseDto> getRecord(@PathVariable Long id) {
        RecordResponseDto response = recordService.get(id);
        return ResponseEntity.ok(response);
    }

    // ✅ 전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<RecordResponseDto>> getAllRecords() {
        List<RecordResponseDto> responses = recordService.getAll();
        return ResponseEntity.ok(responses);
    }

    // ✅ 수정
    @PutMapping("/{id}")
    public ResponseEntity<RecordResponseDto> updateRecord(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody RecordRequestDto request
    ) {
        RecordResponseDto response = recordService.update(id, request, user);
        return ResponseEntity.ok(response);
    }

    // ✅ 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
