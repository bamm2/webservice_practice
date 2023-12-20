package com.yubeom.study.springboot.domain.posts;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest // h2 데이터 베이스를 자동으로 실행해주는 기능도 존재
public class PostsRepositoryTest {

    @Autowired
    PostsRepository repository;

    @After
    public void cleanup() {
        repository.deleteAll(); // 인메모리 db인 h2에 남아 있는 데이터를 다음 테스트를 위해 전부 비워줌
    }

    @Test
    public void builder() {
        Posts actual = Posts.builder()
                .author("작가")
                .title("제목")
                .content("내용")
                .build();

        assertThat(actual).isNotNull();
        assertThat(actual.getAuthor()).isEqualTo("작가");
        assertThat(actual.getTitle()).isEqualTo("제목");
        assertThat(actual.getContent()).isEqualTo("내용");
    }

    @Test
    public void 게시글저장_불러오기() {
        // given
        Posts post = Posts.builder()
                .author("작성자")
                .title("제목 ")
                .content("내용")
                .build();
        repository.save(post);
        repository.flush();
        //when
        Posts savedPost = repository.findById(post.getId()).get();
//        Posts savedPost = repository.getOne(post.getId());
        // findById와 getOne 에 대해 비교 분석 !
        // 트랜잭션의 시작과 끝은 서비스 단에서 하는 게 맞음 ( 하지만 , 서비스 로직과 트랜잭션 로직이 섞임 )
        // 해결 ? -> AOP 프록시(트랜잭션 로직 묶음) -> 비즈로직 -> 레포 로직 !

        //then
        assertThat(savedPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(savedPost.getAuthor()).isEqualTo(post.getAuthor());
    }




}