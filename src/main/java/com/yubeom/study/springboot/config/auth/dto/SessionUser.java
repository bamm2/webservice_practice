package com.yubeom.study.springboot.config.auth.dto;

import com.yubeom.study.springboot.domain.user.User;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) { // 인증된 사용자의 필요한 정보만 갖고 있다.
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
