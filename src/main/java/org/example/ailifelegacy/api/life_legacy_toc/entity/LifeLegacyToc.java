package org.example.ailifelegacy.api.life_legacy_toc.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.ailifelegacy.api.life_legacy_question.entity.LifeLegacyQuestion;
import org.example.ailifelegacy.api.user_case.entity.UserCase;

@Entity
@Table(name = "table_of_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LifeLegacyToc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ManyToMany
    @JoinTable(
        name = "toc_question_mapping", // 조인 테이블 이름
        joinColumns = @JoinColumn(name = "toc_id"), // 현재 엔티티 FK
        inverseJoinColumns = @JoinColumn(name = "question_id") // 반대편 엔티티 FK
    )
    private List<LifeLegacyQuestion> questions = new ArrayList<>();

    @ManyToMany(mappedBy = "tocs")
    private List<UserCase> userCases;
}