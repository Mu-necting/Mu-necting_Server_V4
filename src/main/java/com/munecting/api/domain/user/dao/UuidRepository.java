package com.munecting.api.domain.user.dao;

import com.munecting.api.domain.user.entity.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UuidRepository extends JpaRepository<Uuid, Long> {

    Optional<Uuid> findByUserId(Long userId);
}
