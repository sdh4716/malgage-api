// 경로: com.darong.malgage_api.domain.record.controller
package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.domain.record.dto.RecordRequestDto;
import com.darong.malgage_api.domain.record.dto.RecordResponseDto;
import com.darong.malgage_api.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    // ✅ 레코드 등록
    @PostMapping
    public ResponseEntity<RecordResponseDto> createRecord(@RequestBody RecordRequestDto request) {
        User user = getFakeUser(); // 소셜 로그인 전 임시 유저
        RecordResponseDto response = recordService.create(request, user);
        return ResponseEntity.ok(response);
    }

    // ✅ 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<RecordResponseDto> getRecord(@PathVariable Long id) {
        RecordResponseDto response = recordService.get(id);
        return ResponseEntity.ok(response);
    }

    // ✅ 전체 조회
    @GetMapping
    public ResponseEntity<List<RecordResponseDto>> getAllRecords() {
        List<RecordResponseDto> responses = recordService.getAll();
        return ResponseEntity.ok(responses);
    }

    // ✅ 수정
    @PutMapping("/{id}")
    public ResponseEntity<RecordResponseDto> updateRecord(@PathVariable Long id, @RequestBody RecordRequestDto request) {
        User user = getFakeUser(); // 테스트용 임시 유저
        RecordResponseDto response = recordService.update(id, request, user);
        return ResponseEntity.ok(response);
    }

    // ✅ 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ 임시 유저 생성 (소셜 로그인 연동 전 테스트용)
    private User getFakeUser() {
        return new User(1L, "유나");
    }
}
