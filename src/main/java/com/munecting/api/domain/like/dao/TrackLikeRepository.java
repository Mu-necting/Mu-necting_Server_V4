package com.munecting.api.domain.like.dao;

import com.munecting.api.domain.like.entity.TrackLike;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TrackLikeRepository extends JpaRepository<TrackLike, Long> {

    // todo: 삭제
    int countByTrackId(String trackId);

    Optional<TrackLike> findByTrackId(@NotNull String trackId);

}
