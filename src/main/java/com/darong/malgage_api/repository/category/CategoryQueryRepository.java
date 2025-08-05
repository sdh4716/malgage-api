// domain/category/repository/CategoryQueryRepository.java
package com.darong.malgage_api.repository.category;

import com.darong.malgage_api.controller.dto.response.category.CategoryResponseDto;
import com.darong.malgage_api.controller.dto.response.category.QCategoryResponseDto;
import com.darong.malgage_api.domain.category.*;
import com.darong.malgage_api.domain.user.User;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.darong.malgage_api.domain.category.QCategory.category;

/**
 * Category 관련 복잡한 쿼리를 위한 QueryDSL Repository
 */
@Repository
@RequiredArgsConstructor
public class CategoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자의 모든 카테고리 조회 (기본 + 커스텀)
     * - 기본 카테고리: 모든 사용자가 접근 가능
     * - 커스텀 카테고리: 해당 사용자 소유분만
     *
     * 복잡한 OR 조건이므로 QueryDSL로 처리
     */
    public List<Category> findAllCategoriesForUser(Long userId) {
        return queryFactory
                .selectFrom(category)
                .where(
                        // 기본 카테고리는 모든 사용자가 볼 수 있음
                        category.scope.eq(CategoryScope.DEFAULT)
                                // 또는 사용자의 커스텀 카테고리
                                .or(category.scope.eq(CategoryScope.CUSTOM)
                                        .and(category.user.id.eq(userId)))
                )
                .orderBy(
                        category.scope.asc(),
                        category.type.asc(),
                        category.sortOrder.asc()
                )
                .fetch();
    }

    public List<CategoryResponseDto> findDefaultCategoriesWithVisibility(User user, CategoryType type) {
        QCategory category = QCategory.category;
        QUserCategoryVisibility visibility = QUserCategoryVisibility.userCategoryVisibility;

        return queryFactory
                .select(new QCategoryResponseDto(
                        category.id,
                        category.name,
                        category.type,
                        category.sortOrder,
                        category.scope,
                        category.iconName,
                        category.scope.eq(CategoryScope.DEFAULT),
                        category.scope.eq(CategoryScope.CUSTOM),
                        Expressions.nullExpression(Long.class),
                        category.createdAt,
                        category.updatedAt,
                        visibility.isVisible.coalesce(true) // null일 때 기본값 true
                ))
                .from(category)
                .leftJoin(visibility)
                // ✅ 엔티티 참조 대신 FK 컬럼으로 직접 조인 (지연 로딩 방지)
                .on(visibility.category.id.eq(category.id)    // category 엔티티 접근 대신 id 사용
                        .and(visibility.user.id.eq(user.getId()))) // user 엔티티 접근 대신 id 사용
                .where(
                        category.scope.eq(CategoryScope.DEFAULT),
                        type != null ? category.type.eq(type) : null
                )
                .orderBy(category.type.asc(), category.sortOrder.asc())
                .fetch();
    }

    public List<CategoryResponseDto> findCustomCategoriesWithVisibility(User user, CategoryType type) {
        QCategory category = QCategory.category;
        QUserCategoryVisibility visibility = QUserCategoryVisibility.userCategoryVisibility;

        return queryFactory
                .select(new QCategoryResponseDto(
                        category.id,
                        category.name,
                        category.type,
                        category.sortOrder,
                        category.scope,
                        category.iconName,
                        Expressions.FALSE, // isDefault = false (커스텀이므로)
                        Expressions.TRUE,  // isCustom = true
                        Expressions.constant(user.getId()), // ✅ 상수로 user ID 직접 설정
                        category.createdAt,
                        category.updatedAt,
                        visibility.isVisible.coalesce(true) // 기본값 true
                ))
                .from(category)
                .leftJoin(visibility)
                .on(visibility.category.id.eq(category.id)
                        .and(visibility.user.id.eq(user.getId())))
                .where(
                        category.scope.eq(CategoryScope.CUSTOM),
                        type != null ? category.type.eq(type) : null,
                        // ✅ FK 컬럼으로 직접 필터링 (Category 테이블의 user_id 컬럼)
                        category.user.id.eq(user.getId()) // 이건 단순 where 조건이라 문제없음
                )
                .orderBy(category.type.asc(), category.sortOrder.asc())
                .fetch();
    }

    public List<CategoryResponseDto> findVisibleCategoriesByUserAndType(User user, CategoryType type) {
        QCategory category = QCategory.category;
        QUserCategoryVisibility visibility = QUserCategoryVisibility.userCategoryVisibility;

        return queryFactory
                .select(new QCategoryResponseDto(
                        category.id,
                        category.name,
                        category.type,
                        category.sortOrder,
                        category.scope,
                        category.iconName,
                        category.scope.eq(CategoryScope.DEFAULT),
                        category.scope.eq(CategoryScope.CUSTOM),
                        category.user.id,
                        category.createdAt,
                        category.updatedAt,
                        visibility.isVisible.coalesce(true)  // 가시성 설정이 없으면 true 처리
                ))
                .from(category)
                .leftJoin(visibility)
                .on(visibility.category.id.eq(category.id)
                        .and(visibility.user.id.eq(user.getId())))
                .where(
                        category.type.eq(type),
                        visibility.isVisible.isNull().or(visibility.isVisible.isTrue())  // ← 핵심 조건
                )
                .orderBy(category.sortOrder.asc())
                .fetch();
    }



}