package com.munecting.api.domain.user.service;

import com.munecting.api.domain.comment.dao.CommentRepository;
import com.munecting.api.domain.like.dao.LikeRepository;
import com.munecting.api.domain.uploadedMusic.dao.UploadedMusicRepository;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.dto.request.UpdateProfileRequestDto;
import com.munecting.api.domain.user.dto.response.GetProfileResponseDto;
import com.munecting.api.domain.user.dto.response.UpdateProfileResponseDto;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.munecting.api.global.common.dto.response.Status.USER_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UploadedMusicRepository uploadedMusicRepository;
    private final UserProfileImageService profileImageService;
    private final UserNicknameService nicknameService;

    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserByIdOrThrow(userId);
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

    @Transactional
    public UpdateProfileResponseDto updateProfile(Long userId, UpdateProfileRequestDto requestDto) {
        User user = findUserByIdOrThrow(userId);
        String nickname = updateNickname(user, requestDto.nickname());
        String profileImageUrl = updateProfileImage(user, requestDto.profileImage());

        return UpdateProfileResponseDto.of(nickname, profileImageUrl);
    }

    public User findUserByIdOrThrow (Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    private String updateNickname(User user, String nickname) {
        return nicknameService.updateNickname(user, nickname);
    }

    private String updateProfileImage(User user, MultipartFile imgFile) {
        return profileImageService.updateImage(user, imgFile);
    }

    @Transactional(readOnly = true)
    public GetProfileResponseDto getProfile(Long userId) {
        User user = findUserByIdOrThrow(userId);
        return GetProfileResponseDto.of(user.getProfileImageUrl(), user.getNickname());
    }
}
