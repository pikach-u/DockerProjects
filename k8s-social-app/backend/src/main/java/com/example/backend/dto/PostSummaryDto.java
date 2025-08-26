package com.example.backend.dto;
import com.example.backend.model.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PostSummaryDto {

    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdAt;
    private Long viewCount;
    private String contentPreview;

    public static PostSummaryDto fromEntity(Post post) {
        String contentPreview = post.getContent().length() > 50
                ? post.getContent().substring(0, 50) + "..."
                : post.getContent();

        return PostSummaryDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getAuthor())
                .createdAt(post.getCreatedAt())
                .viewCount(post.getViewCount())
                .contentPreview(contentPreview)
                .build();
    }
}
