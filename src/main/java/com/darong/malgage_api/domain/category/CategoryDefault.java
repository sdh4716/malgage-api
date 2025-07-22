package com.darong.malgage_api.domain.category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryDefault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_default_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    private int sortOrder;

    // ✅ 생성 메서드
    public static CategoryDefault create(String name, int sortOrder, CategoryType type) {
        CategoryDefault category = new CategoryDefault();
        category.name = name;
        category.sortOrder = sortOrder;
        category.type = type;
        return category;
    }
}


