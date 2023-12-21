package com.yubeom.study.springboot.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 파라미터로 선언된 객체에서 사용 허용
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser { // 클래스이름으로 어노테이션 생성
}

// 중복 코드를 어노테이션으로 해결