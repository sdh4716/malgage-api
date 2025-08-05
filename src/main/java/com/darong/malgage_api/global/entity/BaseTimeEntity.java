package com.darong.malgage_api.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 공통 필드인 생성시간, 수정시간을 관리하는 추상 클래스
 *
 * 사용법:
 * 1. 다른 엔티티에서 이 클래스를 상속받으면 자동으로 created_at, updated_at 필드가 추가됨
 * 2. 엔티티가 생성될 때 created_at이 자동 설정
 * 3. 엔티티가 수정될 때 updated_at이 자동 업데이트
 */
@MappedSuperclass // 이 클래스는 테이블로 생성되지 않고, 상속받는 엔티티의 필드로만 사용됨
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 활성화
@Getter
public abstract class BaseTimeEntity {

    /**
     * 엔티티 생성 시간
     * 한 번 설정되면 변경되지 않음 (updatable = false)
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 엔티티 마지막 수정 시간
     * 엔티티가 업데이트될 때마다 자동으로 현재 시간으로 갱신됨
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}