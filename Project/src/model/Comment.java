package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Comment implements Serializable {
    private int reviewerId;
    private String content;
    private final LocalDateTime createdAt;

    public  Comment(int reviewerId, String content) {
        this.reviewerId = reviewerId;
        this.content = content;
        createdAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public int getReviewerId() {
        return reviewerId;
    }
    public String getContent() {
        return content;
    }
}
