package org.example.ailifelegacy.api.auth_identity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import org.example.ailifelegacy.api.user.entity.User;
import org.example.ailifelegacy.common.enums.AuthIdentityEnums.Provider;

@Entity
@Table(name = "auth_identities", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"provider", "provider_uuid"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(name = "provider", length = 50, nullable = false)
    private Provider provider;

    @Column(name = "provider_uuid", length = 255, nullable = false)
    private String providerUuid;

    @Column(name = "password", length = 255)
    private String password; // nullable 허용 (Java에서는 null 기본)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // User와의 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private User user;

    // 생성 시점 자동 세팅 (JPA 라이프사이클 콜백)
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
