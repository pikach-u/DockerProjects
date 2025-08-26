package com.example.backend.controller;


import com.example.backend.dto.PostDto;
import com.example.backend.dto.PostResponseDto;
import com.example.backend.dto.PostSummaryDto;
import com.example.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 게시글 관련 REST API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    /**
     * 헬스체크 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "social-backend");
        status.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(status);
    }

    /**
     * 모든 게시글 조회 API (페이징)
     * GET /api/posts?page=0&size=10
     */
    @GetMapping("/posts")
    public ResponseEntity<Page<PostSummaryDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("게시글 목록 조회 요청 - page: {}, size: {}", page, size);

        try {
            Page<PostSummaryDto> posts = postService.getAllPosts(page, size);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            log.error("게시글 목록 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 새 게시글 생성 API
     * POST /api/posts
     */
    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(@Valid @RequestBody PostDto requestDto) {
        log.info("새 게시글 생성 요청 - 작성자: {}, 제목: {}", requestDto.getAuthor(), requestDto.getTitle());

        try {
            PostResponseDto savedPost = postService.createPost(requestDto);

            Map<String, Object> response = new HashMap<>();
            response.put("id", savedPost.getId());
            response.put("message", "게시글이 성공적으로 생성되었습니다.");
            response.put("post", savedPost);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("게시글 생성 중 오류 발생", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * 특정 게시글 조회 API
     * GET /api/posts/{id}
     */
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> getPost(@PathVariable Long id) {
        log.info("게시글 조회 요청 - ID: {}", id);

        try {
            PostResponseDto post = postService.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (IllegalArgumentException e) {
            log.warn("존재하지 않는 게시글 조회 요청 - ID: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("게시글 조회 중 오류 발생 - ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 작성자별 게시글 조회 API
     * GET /api/posts/author/{author}?page=0&size=10
     */
    @GetMapping("/posts/author/{author}")
    public ResponseEntity<Page<PostSummaryDto>> getPostsByAuthor(
            @PathVariable String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("작성자별 게시글 조회 요청 - 작성자: {}, page: {}, size: {}", author, page, size);

        try {
            Page<PostSummaryDto> posts = postService.getPostsByAuthor(author, page, size);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            log.error("작성자별 게시글 조회 중 오류 발생 - 작성자: {}", author, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 게시글 검색 API
     * GET /api/posts/search?keyword=검색어&page=0&size=10
     */
    @GetMapping("/posts/search")
    public ResponseEntity<Page<PostSummaryDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("게시글 검색 요청 - 키워드: {}, page: {}, size: {}", keyword, page, size);

        try {
            Page<PostSummaryDto> posts = postService.searchPosts(keyword, page, size);
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            log.error("게시글 검색 중 오류 발생 - 키워드: {}", keyword, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 인기 게시글 조회 API
     * GET /api/posts/popular
     */
    @GetMapping("/posts/popular")
    public ResponseEntity<List<PostSummaryDto>> getPopularPosts() {
        log.info("인기 게시글 조회 요청");

        try {
            List<PostSummaryDto> posts = postService.getPopularPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            log.error("인기 게시글 조회 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 작성자 통계 API
     * GET /api/authors/{author}/stats
     */
    @GetMapping("/authors/{author}/stats")
    public ResponseEntity<Map<String, Object>> getAuthorStats(@PathVariable String author) {
        log.info("작성자 통계 조회 요청 - 작성자: {}", author);

        try {
            long postCount = postService.getPostCountByAuthor(author);

            Map<String, Object> stats = new HashMap<>();
            stats.put("author", author);
            stats.put("totalPosts", postCount);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("작성자 통계 조회 중 오류 발생 - 작성자: {}", author, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}