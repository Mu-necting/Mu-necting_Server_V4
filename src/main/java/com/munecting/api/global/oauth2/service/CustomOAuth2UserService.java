package com.munecting.api.global.oauth2.service;

import com.munecting.api.domain.user.dto.SocialType;
import com.munecting.api.domain.user.entity.User;
import com.munecting.api.domain.user.repository.UserRepository;
import com.munecting.api.global.oauth2.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";
    private static final String GOOGLE = "google";
    private static final String SPOTRIFY = "spotify";
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");
        /**
         * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
         * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
         * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
         * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        if (oAuth2User == null) {
            throw new OAuth2AuthenticationException("OAuth2User is null");
        }

        SocialType socialType = getSocialType(userRequest.getClientRegistration().getRegistrationId());
        Map<String, Object> attribute = oAuth2User.getAttributes();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // OAuth2 로그인 시 키(PK)가 되는 값
        OAuth2Response oAuth2Response = oAuth2ResponseKind(socialType, attribute);

        if (oAuth2Response == null) {
            throw new OAuth2AuthenticationException("oAuth2Response is null");
        }

        log.info("socialType = " + socialType);
        log.info("socialId = " + oAuth2Response.getProviderId());
        log.info("attributes ="+ attribute.entrySet()
                .stream()
                .map(entry -> entry.getKey() + " : " + entry.getValue())
                .collect(Collectors.joining("\n")));



        User createdUser = getUser(oAuth2Response, socialType); // getUser() 메소드로 User 객체 생성 후 반환

        return  new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdUser.getRole().getKey())),
                attribute,
                userNameAttributeName,
                createdUser.getEmail(),
                createdUser.getRole(),
                createdUser.getSocialType()
        );
    }
    private SocialType getSocialType(String registrationId) {
        if(NAVER.equals(registrationId)) {
            return SocialType.NAVER;
        }
        if(KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        }
        if(GOOGLE.equals(registrationId)) {
            return SocialType.GOOGLE;
        }
        if(SPOTRIFY.equals(registrationId)) {
            return SocialType.SPOTIFY;
        }
        return null;   // 예외상황 터트리기
    }

    private User getUser(OAuth2Response oAuth2Response,SocialType socialType) {
        User findUser = userRepository.findByEmail(
                oAuth2Response.getProviderId()).orElse(null);

        if(findUser == null) {
            return saveUser(oAuth2Response, socialType);
        }
        return findUser;
    }

    private User saveUser(OAuth2Response oAuth2Response,SocialType socialType) {
        User createdUser = oAuth2Response.toEntity(oAuth2Response, socialType);
        return userRepository.save(createdUser);
    }
    private OAuth2Response oAuth2ResponseKind(SocialType socialType,Map<String, Object> attribute){
        if (socialType == SocialType.NAVER) {
            return new NaverResponse(attribute);
        } else if (socialType == SocialType.GOOGLE) {
            return new GoogleResponse(attribute);
        } else if(socialType == SocialType.KAKAO){
            return new KakaoResponse(attribute);
        } else if(socialType == SocialType.SPOTIFY){
            return new SpotifyResponse(attribute);
        } else{
            return null;
        }
    }
}
