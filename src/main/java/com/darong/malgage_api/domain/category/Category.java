package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Category create(String name, User user) {
        Category category = new Category();
        category.name = name;
        category.user = user;
        return category;
    }
}
