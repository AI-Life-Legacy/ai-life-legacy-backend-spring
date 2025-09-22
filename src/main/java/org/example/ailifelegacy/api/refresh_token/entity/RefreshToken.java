package org.example.ailifelegacy.api.refresh_token.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.example.ailifelegacy.api.user.entity.User;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 유저의 리프레시 토큰인지
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 해시된 토큰 값
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    // 만료 일자
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 생성일자
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 업데이트 메서드 (Dirty Checking)
    public void updateToken(String newTokenHash, LocalDateTime newExpiresAt) {
        this.tokenHash = newTokenHash;
        this.expiresAt = newExpiresAt;
    }

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
