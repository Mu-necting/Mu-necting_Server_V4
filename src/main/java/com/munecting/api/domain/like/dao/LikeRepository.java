package com.munecting.api.domain.like.dao;

import com.munecting.api.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    int countByTrackId(String trackId);

    boolean existsByUserIdAndTrackId(Long userId, String trackId);

    void deleteByTrackIdAndUserId(String trackId, Long userId);
}
