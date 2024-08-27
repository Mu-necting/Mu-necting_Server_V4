package com.munecting.api.domain.like.service;

import com.munecting.api.domain.like.dao.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public void deleteLikesByUserId(Long userId) {
        likeRepository.deleteByUserId(userId);
    }
}
