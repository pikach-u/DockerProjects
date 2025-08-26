package com.example.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_author", columnList = "author"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"content"}) // 내용이 길어질 수 있으므로 toString에서 제외
@EqualsAndHashCode(of = "id")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "내용은 필수 항목입니다.")
    @Size(max = 5000, message = "내용은 5000자를 초과할 수 없습니다.")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @NotBlank(message = "작성자는 필수 항목입니다.")
    @Size(max = 50, message = "작성자명은 50자를 초과할 수 없습니다.")
    @Column(nullable = false, length = 50)
    private String author;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }
}
