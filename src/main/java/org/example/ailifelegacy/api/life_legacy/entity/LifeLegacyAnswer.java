package org.example.ailifelegacy.api.life_legacy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.ailifelegacy.api.life_legacy_question.entity.LifeLegacyQuestion;
import org.example.ailifelegacy.api.user.entity.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "life_legacy_answers",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "question_id"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LifeLegacyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "answer_text", columnDefinition = "TEXT", nullable = false)
    private String answerText;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_answer_user")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "question_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_answer_question")
    )
    private LifeLegacyQuestion question;

    public void updateUserAnswer(String answerText) {
        this.answerText = answerText;
    }
}
