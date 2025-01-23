package com.munecting.api.domain.user.service;

import com.munecting.api.domain.user.dao.UserRepository;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.global.error.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.ThreadLocalRandom;

import static com.munecting.api.global.common.dto.response.Status.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNicknameService {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 15;
    private static final int FIXED_CHARS_LENGTH = 7;
    private static final int SEVEN_DIGIT_MAX_NUM = 9_999_999;
    private static final int EIGHT_DIGIT_MAX_NUM = 99_999_999;
    private static final String DEFAULT_NICKNAME = "뮤넥터";
    private static final String NAME_VALUE_RULES = "^(?=.*[a-zA-Z0-9가-힣])[a-zA-Z가-힣][a-zA-Z0-9가-힣_]*$";
    private static final String SPACE = " ";
    private static final ThreadLocalRandom random = ThreadLocalRandom.current();
    private static final String[] ADJECTIVES = {
            "엉뚱한", "기묘한", "독창적인", "파격적인", "치명적인", "미묘한",
            "경쾌한", "상쾌한", "즐거운", "신나는", "활기찬", "반짝이는",
            "명랑한", "유쾌한", "대담한", "클래식한", "서정적인", "깜찍한", "대단한"
    };

    private final UserRepository userRepository;

    public String generateUniqueNickname() {
        String chars = generateRandomCharacter();
        int num = generateRandomNumber(chars.length());

        return chars + num;
    }

    public String updateNickname(User user, String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return user.getNickname();
        }

        validateNamePolicy(nickname);
        validateDuplicatedNickname(user, nickname);

        return user.updateNickname(nickname);
    }

    private String generateRandomCharacter() {
        int index = random.nextInt(ADJECTIVES.length);
        String adjective = ADJECTIVES[index];

        return adjective + SPACE + DEFAULT_NICKNAME;
    }

    private int generateRandomNumber(int charsLength) {
        int remainLength = MAX_LENGTH - charsLength;
        int maxNumber = remainLength == FIXED_CHARS_LENGTH
                ? EIGHT_DIGIT_MAX_NUM
                : SEVEN_DIGIT_MAX_NUM;

        return random.nextInt(1, maxNumber + 1);
    }

    private void validateNamePolicy(String nickname) {
        if (isInvalidNicknameLength(nickname)) {
            throw new InvalidValueException(INVALID_NICKNAME_LENGTH);
        }

        if (isInvalidNamingRule(nickname)) {
            throw new InvalidValueException(INVALID_NICKNAME_VALUE);
        }
    }

    private void validateDuplicatedNickname(User user, String nickname) {
        if (isDuplicatedNickname(user, nickname)) {
            throw new InvalidValueException(DUPLICATED_NICKNAME);
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
