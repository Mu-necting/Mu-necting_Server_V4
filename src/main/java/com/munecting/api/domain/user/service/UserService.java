package com.munecting.api.domain.user.service;

import com.munecting.api.domain.comment.dao.CommentRepository;
import com.munecting.api.domain.like.dao.LikeRepository;
import com.munecting.api.domain.uploadedMusic.dao.UploadedMusicRepository;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.munecting.api.global.common.dto.response.Status.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UploadedMusicRepository uploadedMusicRepository;

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        deleteUserRelatedEntities(userId);
        userRepository.delete(user);
    }

    private void deleteUserRelatedEntities(Long userId) {
        commentRepository.deleteByUserId(userId);
        likeRepository.deleteByUserId(userId);
        uploadedMusicRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND);
        }
    }
}
