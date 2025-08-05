// domain/category/Category.java
package com.darong.malgage_api.domain.category;

import com.darong.malgage_api.domain.user.User;
import com.darong.malgage_api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "idx_category_user_type_scope", columnList = "user_id, type, scope"),
        @Index(name = "idx_category_default_type", columnList = "type, scope, sort_order")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CategoryType type;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CategoryScope scope;

    /**
     * 카테고리 아이콘 이름 (Material Design Icons 기준)
     * 예: "restaurant", "local_cafe", "directions_bus" 등
     */
    @Column(name = "icon_name", length = 50)
    private String iconName;

    /**
     * 커스텀 카테고리의 소유자 (기본 카테고리는 null)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 이 카테고리에 대한 사용자별 가시성 설정들
     */
    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<UserCategoryVisibility> visibilitySettings = new ArrayList<>();

    // ===== private 생성자 =====
    private Category(String name, CategoryType type, String iconName, Integer sortOrder, CategoryScope scope, User user) {
        validateName(name);
        validateType(type);
        validateScope(scope);
        validateSortOrder(sortOrder);
        validateIconName(iconName);

        this.name = name.trim();
        this.type = type;
        this.iconName = iconName != null ? iconName.trim() : null;
        this.sortOrder = sortOrder != null ? sortOrder : 0;
        this.scope = scope;
        this.user = user;
    }

    // ===== 정적 팩토리 메서드 =====

    /**
     * 기본 카테고리 생성 - 모든 사용자에게 제공되는 카테고리 (아이콘 포함)
     */
    public static Category createDefault(String name, CategoryType type, String iconName, Integer sortOrder) {
        return new Category(name, type, iconName, sortOrder, CategoryScope.DEFAULT, null);
    }

    /**
     * 사용자 커스텀 카테고리 생성 (아이콘 포함)
     */
    public static Category createCustom(String name, CategoryType type, String iconName, User user, Integer sortOrder) {
        validateUser(user);
        Category category = new Category(name, type, iconName, sortOrder, CategoryScope.CUSTOM, user);

        return category;
    }

    /**
     * 사용자 커스텀 카테고리 생성 - 아이콘 없는 버전 (기존 호환성)
     */
    public static Category createCustom(String name, CategoryType type, User user, Integer sortOrder) {
        return createCustom(name, type, null, user, sortOrder);
    }

    // ===== 연관관계 편의 메서드 =====

    /**
     * 가시성 설정 추가
     */
    public void addVisibilitySetting(UserCategoryVisibility visibility) {
        if (visibility.getCategory() == this) {
            this.visibilitySettings.add(visibility);
        }
    }

    // ===== 비즈니스 로직 메서드 =====

    /**
     * 기본 카테고리인지 확인
     */
    public boolean isDefaultCategory() {
        return this.scope == CategoryScope.DEFAULT;
    }

    /**
     * 커스텀 카테고리인지 확인
     */
    public boolean isCustomCategory() {
        return this.scope == CategoryScope.CUSTOM;
    }

    /**
     * 특정 사용자의 카테고리인지 확인
     */
    public boolean belongsToUser(Long userId) {
        return this.user != null && this.user.getId().equals(userId);
    }

    /**
     * 아이콘이 설정되어 있는지 확인
     */
    public boolean hasIcon() {
        return this.iconName != null && !this.iconName.trim().isEmpty();
    }

    /**
     * 카테고리 정보 수정 (커스텀 카테고리만 수정 가능)
     */
    public void updateCategory(String name, String iconName, Integer sortOrder) {
        validateCustomCategory();

        if (name != null && !name.trim().isEmpty()) {
            validateName(name);
            this.name = name.trim();
        }
        if (iconName != null) {
            validateIconName(iconName);
            this.iconName = iconName.trim().isEmpty() ? null : iconName.trim();
        }
        if (sortOrder != null) {
            validateSortOrder(sortOrder);
            this.sortOrder = sortOrder;
        }
    }

    /**
     * 카테고리 정보 수정 - 아이콘 없는 버전 (기존 호환성)
     */
    public void updateCategory(String name, Integer sortOrder) {
        updateCategory(name, this.iconName, sortOrder);
    }

    /**
     * 정렬 순서 변경
     */
    public void updateSortOrder(Integer sortOrder) {
        validateSortOrder(sortOrder);
        this.sortOrder = sortOrder;
    }

    /**
     * 아이콘 변경
     */
    public void updateIcon(String iconName) {
        validateIconName(iconName);
        this.iconName = iconName != null && !iconName.trim().isEmpty() ? iconName.trim() : null;
    }

    // ===== 유효성 검증 메서드 =====

    private void validateCustomCategory() {
        if (this.scope != CategoryScope.CUSTOM) {
            throw new IllegalStateException("기본 카테고리는 수정할 수 없습니다.");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리명은 필수입니다.");
        }
        if (name.trim().length() > 50) {
            throw new IllegalArgumentException("카테고리명은 50자를 초과할 수 없습니다.");
        }
    }

    private static void validateType(CategoryType type) {
        if (type == null) {
            throw new IllegalArgumentException("카테고리 타입은 필수입니다.");
        }
    }

    private static void validateScope(CategoryScope scope) {
        if (scope == null) {
            throw new IllegalArgumentException("카테고리 범위는 필수입니다.");
        }
    }

    private static void validateSortOrder(Integer sortOrder) {
        if (sortOrder != null && sortOrder < 0) {
            throw new IllegalArgumentException("정렬 순서는 0 이상이어야 합니다.");
        }
    }

    private static void validateIconName(String iconName) {
        if (iconName != null && iconName.trim().length() > 50) {
            throw new IllegalArgumentException("아이콘명은 50자를 초과할 수 없습니다.");
        }
    }

    private static void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("커스텀 카테고리는 사용자 정보가 필수입니다.");
        }
    }

    // ===== Object 메서드 오버라이드 =====

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return id != null && id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Category{id=%d, name='%s', type=%s, scope=%s, iconName='%s'}",
                id, name, type, scope, iconName);
    }
}