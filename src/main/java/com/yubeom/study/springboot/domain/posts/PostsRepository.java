package com.yubeom.study.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    // JpaRepository<Entity 클래스 , PK 타입>
    // @Repository 어노테이션 불필요
}
