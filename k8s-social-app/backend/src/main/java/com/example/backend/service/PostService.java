package com.example.backend.service;

import com.example.backend.dto.PostDto;
import com.example.backend.dto.PostResponseDto;
import com.example.backend.dto.PostSummaryDto;
import com.example.backend.model.Post;
import com.example.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    public Page<PostSummaryDto> getAllPosts(int page, int size) {
        log.debug("모든 게시글 조회 - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);

        return posts.map(PostSummaryDto::fromEntity);
    }

    @Transactional
    public PostResponseDto createPost(PostDto requestDto) {
        log.info("새 게시글 생성 - 작성자: {}, 제목: {}", requestDto.getAuthor(), requestDto.getTitle());

        Post post = Post.builder()
                .title(requestDto.getTitle().trim())
                .content(requestDto.getContent().trim())
                .author(requestDto.getAuthor().trim())
                .build();

        Post savedPost = postRepository.save(post);
        log.info("게시글 생성 완료 - ID: {}", savedPost.getId());

        return PostResponseDto.fromEntity(savedPost);
    }

    @Transactional
    public PostResponseDto getPostById(Long id) {
        log.debug("게시글 조회 - ID: {}", id);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));

        post.incrementViewCount();
        postRepository.save(post);

        return PostResponseDto.fromEntity(post);
    }

    public Page<PostSummaryDto> getPostsByAuthor(String author, int page, int size) {
        log.debug("작성자별 게시글 조회 - 작성자: {}, page: {}, size: {}", author, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(author, pageable);

        return posts.map(PostSummaryDto::fromEntity);
    }

    public Page<PostSummaryDto> searchPosts(String keyword, int page, int size) {
        log.debug("게시글 검색 - 키워드: {}, page: {}, size: {}", keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByKeyword(keyword, pageable);

        return posts.map(PostSummaryDto::fromEntity);
    }

    public List<PostSummaryDto> getPopularPosts() {
        log.debug("인기 게시글 조회");

        List<Post> posts = postRepository.findTop10ByOrderByViewCountDesc();
        return posts.stream()
                .map(PostSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public long getPostCountByAuthor(String author) {
        return postRepository.countByAuthor(author);
    }
}