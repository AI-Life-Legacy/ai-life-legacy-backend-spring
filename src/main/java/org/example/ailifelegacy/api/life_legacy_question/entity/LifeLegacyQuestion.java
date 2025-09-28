package org.example.ailifelegacy.api.life_legacy_question.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import lombok.*;
import java.util.List;
import org.example.ailifelegacy.api.life_legacy.entity.LifeLegacyAnswer;
import org.example.ailifelegacy.api.life_legacy_toc.entity.LifeLegacyToc;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LifeLegacyQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", length = 500, nullable = false)
    private String questionText;

    @ManyToMany(mappedBy = "questions")
    private List<LifeLegacyToc> tocs = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LifeLegacyAnswer> answers;
}

