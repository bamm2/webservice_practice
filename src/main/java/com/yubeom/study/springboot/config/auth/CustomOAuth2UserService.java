package com.yubeom.study.springboot.config.auth;

import com.yubeom.study.springboot.config.auth.dto.OAuthAttributes;
import com.yubeom.study.springboot.config.auth.dto.SessionUser;
import com.yubeom.study.springboot.domain.user.User;
import com.yubeom.study.springboot.domain.user.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

import javax.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 네이버 , 구글 등 서비스 구분 id
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName(); // pk 역할을 하는 값 , 구글은("sub") , 네이버,카카오는 지원 X

        // OAuth2UserService 를 통해 가져온 attribute 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes); // 업데이트가 반영된 유저 엔티티

        // 세션에 사용자 정보를 저장하기 위한 Dto 클래스 ( 유저 클래스를 직접 사용하면 직렬화 문제 발생 ) 
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    // 사용자의 이름 혹은 사진이 변경되면 반영하기 위한 기능
    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity()); // 최초 가입이라면 GUEST 권한 부여

        return userRepository.save(user);
    }
}
