package com.munecting.api.domain.user.service;

import com.munecting.api.domain.user.dto.Role;
import com.munecting.api.domain.user.dto.SocialType;
import com.munecting.api.domain.user.dto.UserRegisterDto;
import com.munecting.api.domain.user.dto.UserResponseDto;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.domain.user.repository.UserRepository;
import com.munecting.api.global.common.dto.response.GeneralException;
import com.munecting.api.global.common.dto.response.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder customPasswordEncoder;

    /**
     * 유저의 회원가입 메소드
     * @param userRegisterDto
     * @throws Exception
     */
    public UserResponseDto register(UserRegisterDto userRegisterDto){
        if (userRepository.existsByEmail(userRegisterDto.getEmail())) {
            throw new GeneralException(Status.ALREADY_EXISTS_EMAIL);
        }
        if (userRepository.existsByNickname(userRegisterDto.getNickname())) {
            throw new GeneralException(Status.ALREADY_EXISTS_NICKNAME);
        }
        User user = User.builder()
                .email(userRegisterDto.getEmail())
                .password(passwordEncode(userRegisterDto.getPassword()))
                .nickname(userRegisterDto.getNickname())
                .role(Role.USER)
                .socialType(SocialType.EMAIL)
                .build();
        userRepository.save(user);


        return UserResponseDto.builder()
                .email(userRegisterDto.getEmail())
                .nickname(userRegisterDto.getNickname())
                .build();
    }

    /**
     * 유저의 패스워드를 인코드
     * 인코드 후 User.buider()
     * @param password
     * @return
     */
    private String passwordEncode(String password) {
        return customPasswordEncoder.encode(password);
    }




}
