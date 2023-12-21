package com.yubeom.study.springboot.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yubeom.study.springboot.domain.posts.Posts;
import com.yubeom.study.springboot.domain.posts.PostsRepository;
import com.yubeom.study.springboot.web.dto.PostsResponseDto;
import com.yubeom.study.springboot.web.dto.PostsSaveRequestDto;
import com.yubeom.study.springboot.web.dto.PostsUpdateRequestDto;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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

    // @WithMockUser 는 MockMvc 에서만 작동 , @Before: 테스트 시작시 Mockmvc 인스턴스 생성
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        repository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER") // 스프링 시큐리티 테스트용 목 객체 , "유저" 권한 획득
    public void Posts_등록된다() throws Exception {
        //given
        PostsSaveRequestDto dto = PostsSaveRequestDto.builder()
                .title(TITLE)
                .content(CONTENT)
                .author(AUTHOR)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
//        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, dto, Long.class);

        // 시큐리티 적용 후 추가
        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        //then
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = repository.findAll();
        assertThat(all.size()).isEqualTo(1);
        assertThat(all.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(all.get(0).getContent()).isEqualTo(CONTENT);
    }

    @Test
    @WithMockUser(roles = "USER") // 스프링 시큐리티 테스트용 , "유저" 권한 획득
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
//        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class);

        // 시큐리티 적용 후 추가
        mvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        //then
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        Posts updatedPosts = repository.findAll().get(0);
        assertThat(updatedPosts.getTitle()).isEqualTo(updateTitle);
        assertThat(updatedPosts.getContent()).isEqualTo(updateContent);
    }

    @Test
    @WithMockUser(roles = "USER") // 스프링 시큐리티 테스트용 , "유저" 권한 획득
    public void findById() throws Exception {
        //given
        Posts post = Posts.builder()
                .author(AUTHOR)
                .title(TITLE)
                .content(CONTENT)
                .build();

        repository.save(post);

        String url = "http://localhost:" + port + "/api/v1/posts/" + post.getId();

        //when
//        ResponseEntity<PostsResponseDto> responseDto = restTemplate.getForEntity(url, PostsResponseDto.class, post.getId());

        // 시큐리티 적용 후 추가 when && then 테스트를 위해 toString 을 오버라이딩 했다.
        mvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(post.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(post.toString()));

        //then
//        assertThat(responseDto.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseDto.getBody().getAuthor()).isEqualTo(AUTHOR);
//        assertThat(responseDto.getBody().getTitle()).isEqualTo(TITLE);

    }

    @Test
    @WithMockUser(roles = "USER") // 스프링 시큐리티 테스트용 , "유저" 권한 획득
    public void delete() throws Exception {
        //given
        Posts post = Posts.builder()
                .author(AUTHOR)
                .title(TITLE)
                .content(CONTENT)
                .build();

        repository.save(post);

        String url = "http://localhost:" + port + "/api/v1/posts/" + post.getId();

        String deletedId = String.valueOf(post.getId());

        //when
//        ResponseEntity<Long> response = restTemplate.exchange(url, HttpMethod.DELETE, HttpEntity.EMPTY, Long.class);

        // 시큐리티 적용 후 추가 when && then
        mvc.perform(MockMvcRequestBuilders.delete(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(post.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string(deletedId));

        //then
//        assertThat(resultActions.equals(post.getId())).isTrue();
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isEqualTo(post.getId()); // 삭제된 id값 반환 일치 여부 확인
    }

}