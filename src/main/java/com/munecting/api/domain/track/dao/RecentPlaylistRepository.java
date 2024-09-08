package com.munecting.api.domain.track.dao;

import com.munecting.api.domain.track.domain.RecentlyPlayedTrack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentPlaylistRepository extends JpaRepository<RecentlyPlayedTrack, Long> {

    int countByUserId(Long userId);

    @Query("SELECT rt.trackId FROM RecentlyPlayedTrack rt WHERE rt.userId = :userId ORDER BY rt.createdAt ASC, rt.id ASC")
    List<String> findOldestTracks(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT rt.trackId FROM RecentlyPlayedTrack rt WHERE rt.userId = :userId")
    List<String> findTrackIdsByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM RecentlyPlayedTrack rt WHERE rt.userId = :userId AND rt.trackId IN (:trackIds)")
    void deleteAllByUserIdAndTrackIds(@Param("userId") Long userId, @Param("trackIds") List<String> trackIds);
}
