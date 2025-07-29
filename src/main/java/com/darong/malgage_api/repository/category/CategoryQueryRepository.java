// domain/category/repository/CategoryQueryRepository.java
package com.darong.malgage_api.repository.category;

import com.darong.malgage_api.domain.category.Category;
import com.darong.malgage_api.domain.category.CategoryScope;
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
}