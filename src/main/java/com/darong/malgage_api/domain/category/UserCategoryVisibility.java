// domain/category/UserCategoryVisibility.java
package com.darong.malgage_api.domain.category;

import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자별 카테고리 가시성 관리 엔티티
 * 기본 카테고리의 숨김/표시 상태만 관리
 */
@Entity
@Table(name = "user_category_visibility",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_category", columnNames = {"user_id", "category_id"})
        },
        indexes = {
                @Index(name = "idx_user_type_visible", columnList = "user_id, category_type, is_visible")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCategoryVisibility extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visibility_id")
    private Long id;

    /**
     * 사용자 참조
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 카테고리 참조
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * 카테고리 타입 (성능 최적화를 위한 비정규화)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category_type", nullable = false, length = 10)
    private CategoryType categoryType;

    /**
     * 사용자에게 표시되는지 여부
     */
    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    // ===== private 생성자 =====
    private UserCategoryVisibility(User user, Category category, Boolean isVisible) {
        validateUser(user);
        validateCategory(category);

        this.user = user;
        this.category = category;
        this.categoryType = category.getType(); // 비정규화
        this.isVisible = isVisible != null ? isVisible : true;
    }

    // ===== 정적 팩토리 메서드 =====

    /**
     * 기본 가시성 설정 생성 (표시 상태)
     */
    public static UserCategoryVisibility createVisible(User user, Category category) {
        UserCategoryVisibility visibility = new UserCategoryVisibility(user, category, true);

        // 연관관계 편의 메서드 호출
        user.addCategoryVisibilitySetting(visibility);
        category.addVisibilitySetting(visibility);

        return visibility;
    }

    /**
     * 숨김 상태로 가시성 설정 생성
     */
    public static UserCategoryVisibility createHidden(User user, Category category) {
        UserCategoryVisibility visibility = new UserCategoryVisibility(user, category, false);

        // 연관관계 편의 메서드 호출
        user.addCategoryVisibilitySetting(visibility);
        category.addVisibilitySetting(visibility);

        return visibility;
    }

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 카테고리 숨김 (기본 카테고리만 가능)
     */
    public void hide() {
        if (!this.category.isDefaultCategory()) {
            throw new IllegalStateException("기본 카테고리만 숨길 수 있습니다.");
        }
        this.isVisible = false;
    }

    /**
     * 카테고리 표시
     */
    public void show() {
        this.isVisible = true;
    }

    /**
     * 가시성 토글
     */
    public void toggleVisibility() {
        if (!this.category.isDefaultCategory()) {
            throw new IllegalStateException("기본 카테고리만 가시성을 변경할 수 있습니다.");
        }
        this.isVisible = !this.isVisible;
    }

    // ===== 유효성 검증 메서드 =====
    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }
    }

    private static void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("카테고리 정보는 필수입니다.");
        }
    }

    // ===== 조회 편의 메서드 =====
    public boolean isHidden() {
        return !this.isVisible;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    public Long getCategoryId() {
        return this.category.getId();
    }

    public String getCategoryName() {
        return this.category.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserCategoryVisibility that = (UserCategoryVisibility) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}