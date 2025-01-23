package com.munecting.api.domain.user.service;

import com.munecting.api.domain.user.constant.Role;
import com.munecting.api.domain.user.constant.SocialType;
import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.global.error.exception.NicknameException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCreateService {

    private final UserRepository userRepository;
    private final UserNicknameService nicknameService;

    @Transactional(propagation = REQUIRES_NEW)
    @Retryable(
            retryFor = DataIntegrityViolationException.class,
            maxAttempts = 20,
            backoff = @Backoff(delay = 100)
    )
    public User createUser(String socialId, SocialType socialType) {
        User newUser = User.toEntity(
                socialId,
                nicknameService.generateUniqueNickname(),
                Role.USER,
                socialType
        );

        return userRepository.save(newUser);
    }

    @Recover
    public User recoverFromCreateUserException(DataIntegrityViolationException e, String socialId, SocialType socialType) {
        log.error("소셜 ID: {} - 닉네임 생성 오류가 발생했습니다.", socialId, e);
        throw new NicknameException();
    }

}
