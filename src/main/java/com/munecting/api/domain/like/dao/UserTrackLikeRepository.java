package com.munecting.api.domain.like.dao;

import com.munecting.api.domain.like.entity.UserTrackLike;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTrackLikeRepository extends JpaRepository<UserTrackLike, Long> {

    boolean existsByUserIdAndTrackId(Long userId, String trackId);

    Slice<UserTrackLike> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT l from UserTrackLike l where l.userId = :userId and l.id < :id")
    Slice<UserTrackLike> findByUserId(@Param("userId") Long userId, @Param("id") Long cursor, Pageable pageable);

    void deleteByUserId(Long userId);

    Optional<UserTrackLike> findByTrackIdAndUserId(@NotNull String trackId, @NotNull Long userId);

}
