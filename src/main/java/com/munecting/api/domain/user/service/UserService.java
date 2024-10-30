package com.munecting.api.domain.user.service;

import com.munecting.api.domain.comment.dao.CommentRepository;
import com.munecting.api.domain.like.dao.LikeRepository;
import com.munecting.api.domain.uploadedMusic.dao.UploadedMusicRepository;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.dto.request.UpdateProfileRequestDto;
import com.munecting.api.domain.user.dto.response.UpdateProfileResponseDto;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import com.munecting.api.global.error.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
    private final UserProfileImageService userProfileImageService;

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 15;

    private static final String NAME_VALUE_RULES = "^[a-zA-Z0-9가-힣]+$";

    private static final String NICKNAME_LENGTH_ERROR_MESSAGE = "닉네임은 2-15자 사이여야 합니다.";
    private static final String NICKNAME_WRONG_VALUE_ERROR_MESSAGE = "닉네임은 한글, 영문, 숫자만 포함할 수 있습니다.";
    private static final String NICKNAME_DUPLICATED_ERROR_MESSAGE = "이미 사용 중인 닉네임입니다.";


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

    @Transactional
    public UpdateProfileResponseDto updateProfile(Long userId, UpdateProfileRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        String nickname = updateNickname(user, requestDto.nickname());
        String profileImageUrl = updateProfileImage(user, requestDto.profileImage());

        return UpdateProfileResponseDto.of(nickname, profileImageUrl);
    }

    private String updateNickname(User user, String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return user.getNickname();
        }

        validateNickname(user, nickname);
        return user.updateNickname(nickname);
    }

    private void validateNickname(User existingUser, String nickname) {
        if (nickname.length() < MIN_LENGTH || nickname.length() > MAX_LENGTH) {
            throw new InvalidValueException(Status.BAD_REQUEST, NICKNAME_LENGTH_ERROR_MESSAGE);
        }

        if (!nickname.matches(NAME_VALUE_RULES)) {
            throw new InvalidValueException(Status.BAD_REQUEST, NICKNAME_WRONG_VALUE_ERROR_MESSAGE);
        }

        // 중복 검사
        if (!existingUser.getNickname().equals(nickname)
                && userRepository.existsByNickname(nickname)) {
            throw new InvalidValueException(Status.CONFLICT, NICKNAME_DUPLICATED_ERROR_MESSAGE);
        }
    }

    private String updateProfileImage(User user, MultipartFile imgFile) {
        return userProfileImageService.updateImage(user, imgFile);
    }
}
