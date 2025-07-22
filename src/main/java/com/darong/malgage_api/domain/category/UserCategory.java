package com.darong.malgage_api.domain.category;

import com.darong.malgage_api.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_category_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_category_id")  // 선택적으로 default 참조
    private CategoryDefault baseCategory;

    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;

    public static UserCategory create(String name, User user, CategoryDefault baseCategory) {
        UserCategory category = new UserCategory();
        category.name = name;
        category.user = user;
        category.baseCategory = baseCategory;
        category.enabled = true;
        return category;
    }

    public void disable() {
        this.enabled = false;
    }
}
