package com.munecting.api.domain.like.dao;

import com.munecting.api.domain.like.entity.UserTrackLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTrackLikeRepository extends JpaRepository<UserTrackLike, Long> {

}
