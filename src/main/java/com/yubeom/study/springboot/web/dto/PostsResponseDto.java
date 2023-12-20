package com.yubeom.study.springboot.web.dto;

import com.yubeom.study.springboot.domain.posts.Posts;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // findById() 테스트 중에 매핑이 잘 안되어 검색해보니 ,
                // ObjectMapper가 내부적으로 json을 java로 변환하는 과정에서 오류가 발생해서 기본 생성자를 생성해줘야 했다 .
public class PostsResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;

    public PostsResponseDto(Posts posts){
        this.id =posts.getId();
        this.title=posts.getTitle();
        this.content=posts.getContent();
        this.author=posts.getAuthor();
    }
}
