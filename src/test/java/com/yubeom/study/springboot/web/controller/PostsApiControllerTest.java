package com.yubeom.study.springboot.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.yubeom.study.springboot.domain.posts.Posts;
import com.yubeom.study.springboot.domain.posts.PostsRepository;
import com.yubeom.study.springboot.web.dto.PostsResponseDto;
import com.yubeom.study.springboot.web.dto.PostsSaveRequestDto;
import com.yubeom.study.springboot.web.dto.PostsUpdateRequestDto;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)  // JPA는 WebMvcTest로 테스트 불가능
public class PostsApiControllerTest {

    private static final String CONTENT = "content";
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";


    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PostsRepository repository;

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void Posts_등록된다() throws Exception {
        //given
        PostsSaveRequestDto dto = PostsSaveRequestDto.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, dto, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = repository.findAll();
        assertThat(all.size()).isEqualTo(1);
        assertThat(all.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(all.get(0).getContent()).isEqualTo(CONTENT);
    }

    @Test
    public void Post_수정완료() throws Exception {
        //given
        Posts savedPost = repository.save(Posts.builder()
                .content(CONTENT)
                .author(AUTHOR)
                .title(TITLE)
                .build());

        Long updateId = savedPost.getId();
        String updateTitle = "updateTitle";
        String updateContent = "updateContent";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(updateTitle)
                .content(updateContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        Posts updatedPosts = repository.findAll().get(0);
        assertThat(updatedPosts.getTitle()).isEqualTo(updateTitle);
        assertThat(updatedPosts.getContent()).isEqualTo(updateContent);
    }

    @Test
    public void findById() {
        //given
        Posts post = Posts.builder()
                .author(AUTHOR)
                .title(TITLE)
                .content(CONTENT)
                .build();

        repository.save(post);

        String url = "http://localhost:" + port + "/api/v1/posts/" + post.getId();

        //when
        ResponseEntity<PostsResponseDto> responseDto = restTemplate.getForEntity(url, PostsResponseDto.class, post.getId());

        //then
        assertThat(responseDto.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseDto.getBody().getAuthor()).isEqualTo(AUTHOR);
        assertThat(responseDto.getBody().getTitle()).isEqualTo(TITLE);
    }

    @Test
    public void delete() {
        //given
        Posts post = Posts.builder()
                .author(AUTHOR)
                .title(TITLE)
                .content(CONTENT)
                .build();

        repository.save(post);

        String url = "http://localhost:" + port + "/api/v1/posts/" + post.getId();

        //when
        ResponseEntity<Long> response = restTemplate.exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, Long.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(post.getId()); // 삭제된 id값 반환 일치 여부 확인
    }

}