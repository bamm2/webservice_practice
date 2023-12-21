package com.yubeom.study.springboot.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반 사용자");

    private final String key; // 스프링 시큐리티에서는 항상 ROLE_ 이 앞에 존재해야 한다.
    private final String title;

}
