package com.munecting.api.domain.user.service;

import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.global.common.dto.response.Status;
import com.munecting.api.global.error.exception.InternalServerException;
import com.munecting.api.global.error.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNicknameService {

    private final UserRepository userRepository;

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 15;

    private static final String NAME_VALUE_RULES = "^(?=.*[a-zA-Z0-9가-힣])[a-zA-Z가-힣][a-zA-Z0-9가-힣_]*$";
    private static final String NICKNAME_LENGTH_ERROR_MESSAGE = "닉네임은 2-15자 사이여야 합니다.";
    private static final String NICKNAME_WRONG_VALUE_ERROR_MESSAGE = "닉네임은 한글, 영문, 숫자, 언더바(_)만 사용 가능하며, 첫 글자는 언더바 또는 숫자일 수 없습니다.";
    private static final String NICKNAME_DUPLICATED_ERROR_MESSAGE = "이미 사용 중인 닉네임입니다.";

    public String updateNickname(User user, String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return user.getNickname();
        }

        validateNickname(user, nickname);
        return user.updateNickname(nickname);
    }

    private void validateNickname(User existingUser, String nickname) {
        if (isInvalidNicknameLength(nickname)) {
            throw new InvalidValueException(Status.BAD_REQUEST, NICKNAME_LENGTH_ERROR_MESSAGE);
        }

        if (isInvalidNamingRule(nickname)) {
            throw new InvalidValueException(Status.BAD_REQUEST, NICKNAME_WRONG_VALUE_ERROR_MESSAGE);
        }

        if (isDuplicatedNickname(existingUser, nickname)) {
            throw new InvalidValueException(Status.CONFLICT, NICKNAME_DUPLICATED_ERROR_MESSAGE);
        }
    }

    private boolean isInvalidNicknameLength(String nickname) {
        return nickname.length() < MIN_LENGTH || nickname.length() > MAX_LENGTH;
    }

    private boolean isInvalidNamingRule(String nickname) {
        return !nickname.matches(NAME_VALUE_RULES);
    }

    private boolean isDuplicatedNickname(User existingUser, String nickname) {
        // 기존 닉네임과 일치하는 경우 중복 체크 제외
        if (existingUser.getNickname().equals(nickname)) {
            return false;
        }

        return userRepository.existsByNickname(nickname);
    }
}
