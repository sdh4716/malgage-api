package com.darong.malgage_api.auth.security;

import com.darong.malgage_api.domain.user.User;

import java.util.Collections;

public class UserPrincipal extends org.springframework.security.core.userdetails.User {

    private final Long userId; // <-- JPA User의 id

    public UserPrincipal(User userEntity) {
        super(userEntity.getEmail(), "", Collections.emptyList());
        this.userId = userEntity.getId();
    }

    public Long getUserId() {
        return userId;
    }
}