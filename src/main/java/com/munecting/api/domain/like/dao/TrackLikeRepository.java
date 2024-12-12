package com.munecting.api.domain.like.dao;

import com.munecting.api.domain.like.entity.TrackLike;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TrackLikeRepository extends JpaRepository<TrackLike, Long> {

    int countByTrackId(String trackId);

    boolean existsByUserIdAndTrackId(Long userId, String trackId);

    void deleteByTrackIdAndUserId(String trackId, Long userId);

    Slice<TrackLike> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT l from TrackLike l where l.userId = :userId and l.id < :id")
    Slice<TrackLike> findByUserId(@Param("userId") Long userId, @Param("id") Long cursor, Pageable pageable);

    void deleteByUserId(Long userId);
}
