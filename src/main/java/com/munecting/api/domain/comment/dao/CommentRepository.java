package com.munecting.api.domain.comment.dao;

import com.munecting.api.domain.comment.entity.Comment;
import com.munecting.api.domain.playList.entity.Playlist;
import com.munecting.api.domain.playListMusic.entity.PlaylistMusic;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.trackId = :trackId AND (:cursorStr IS NULL OR c.createdAt < CAST(:cursorStr AS TIMESTAMP)) ORDER BY c.createdAt DESC")
    Page<Comment> findCommentsByTrackIdWithCursor(
            @Param("trackId") String trackId, @Param("cursorStr") String cursorStr, Pageable pageable);
}
