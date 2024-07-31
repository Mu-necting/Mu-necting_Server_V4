package com.munecting.api.domain.playListMusic.dao;

import com.munecting.api.domain.playListMusic.entity.PlaylistMusic;
import com.munecting.api.domain.playListMusic.entity.PlaylistMusicId;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistMusicRepository extends JpaRepository<PlaylistMusic, PlaylistMusicId> {

    @Query("SELECT pm FROM PlaylistMusic pm WHERE pm.id.playlistId = :playlistId AND pm.playOrder > :playOrder")
    Page<PlaylistMusic> findByPlaylistIdAndOrderGreaterThan(@Param("playlistId") Long playlistId, @Param("playOrder") Long playOrder, Pageable pageable);

    @Query("SELECT COUNT(pm) FROM PlaylistMusic pm WHERE pm.id.playlistId = :playlistId")
    long countByPlaylistId(@Param("playlistId") Long playlistId);
}
