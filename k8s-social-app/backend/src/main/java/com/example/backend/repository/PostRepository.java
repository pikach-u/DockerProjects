package com.example.backend.repository;
import com.example.backend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 페이징을 지원하는 전체 게시글 조회 (최신순)
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 작성자별 게시글 조회 (페이징 지원)
    Page<Post> findByAuthorOrderByCreatedAtDesc(String author, Pageable pageable);

    // 제목으로 검색 (대소문자 구분 없음, 페이징 지원)
    Page<Post> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title, Pageable pageable);

    // 제목 또는 내용으로 검색
    @Query("SELECT p FROM Post p WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 특정 기간 내 게시글 조회
    List<Post> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    // 조회수 상위 게시글 조회
    List<Post> findTop10ByOrderByViewCountDesc();

    // 조회수 증가 쿼리
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // 작성자별 게시글 수 조회
    long countByAuthor(String author);
}
