package com.munecting.api.domain.user.repository;

import com.munecting.api.domain.user.dto.SocialType;
import com.munecting.api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

}
