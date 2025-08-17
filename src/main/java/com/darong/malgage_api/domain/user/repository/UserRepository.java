package com.darong.malgage_api.domain.user.repository;

import com.darong.malgage_api.domain.user.AuthProvider;
import com.darong.malgage_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

     /* 반환 타입: Optional<User>
     * - 결과가 존재할 수 있음: Optional에 값(User) 존재
     * - 결과가 없을 수 있음: Optional.empty() 로 반환됨
     * - NPE(NullPointerException) 방지에 유용
     **/
    Optional<User> findByOauthIdAndProvider(String oauthId, AuthProvider provider);
}
