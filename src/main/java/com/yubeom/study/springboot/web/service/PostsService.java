package com.yubeom.study.springboot.web.service;

import com.yubeom.study.springboot.domain.posts.Posts;
import com.yubeom.study.springboot.domain.posts.PostsRepository;
import com.yubeom.study.springboot.web.dto.PostsResponseDto;
import com.yubeom.study.springboot.web.dto.PostsSaveRequestDto;
import com.yubeom.study.springboot.web.dto.PostsUpdateRequestDto;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository repository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return repository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto) {
        Posts posts = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id = " + id));
        posts.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }

    // 조회의 경우 은행과 같이 정산할 때 변경되야하는 경우가 아닌 경우는 트랜잭션 락을 획득하지 않아도 됨
    public PostsResponseDto findById(Long id) {
        Posts entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다. id = " + id));

        return new PostsResponseDto(entity);
    }
    
}
