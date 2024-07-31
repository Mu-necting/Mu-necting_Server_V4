package com.munecting.api.domain.playList.dao;

import com.munecting.api.domain.playList.entity.Playlist;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    @Query("SELECT p FROM Playlist p WHERE p.userId = :user_id AND p.id > :id ORDER BY p.id ASC")
    List<Playlist> findByUserIdAndIdAfter(@Param("user_id") Long userId, @Param("id") Long id, Pageable pageable);

}
