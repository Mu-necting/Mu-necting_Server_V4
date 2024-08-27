package com.munecting.api.domain.uploadedMusic.dao;

import com.munecting.api.domain.uploadedMusic.entity.UploadedMusic;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadedMusicRepository extends JpaRepository<UploadedMusic, Long> {

    @Query(
            value = "SELECT * FROM (" +
                    "    SELECT m.*, " +
                    "    (6371 * ACOS(COS(RADIANS(:latitude)) " +
                    "    * COS(RADIANS(m.latitude)) " +
                    "    * COS(RADIANS(m.longitude) - RADIANS(:longitude)) " +
                    "    + SIN(RADIANS(:latitude)) " +
                    "    * SIN(RADIANS(m.latitude)))) AS distance " +
                    "    FROM uploaded_music m " +
                    ") AS subquery " +
                    "WHERE subquery.distance <= :radius",
            nativeQuery = true)
    List<UploadedMusic> findUploadedMusicByLocationAndRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") Integer radius
    );

    void deleteByUserId(Long userId);
}
