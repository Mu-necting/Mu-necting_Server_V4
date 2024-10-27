package com.munecting.api.domain.like.dao;

import com.munecting.api.domain.like.entity.Like;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface LikeRepository extends JpaRepository<Like, Long> {

    int countByTrackId(String trackId);

    boolean existsByUserIdAndTrackId(Long userId, String trackId);

    void deleteByTrackIdAndUserId(String trackId, Long userId);

    Slice<Like> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT l from Like l where l.userId = :userId and l.id < :id")
    Slice<Like> findByUserId(@Param("userId") Long userId, @Param("id") Long cursor, Pageable pageable);

    void deleteByUserId(Long userId);
}
