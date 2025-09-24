package org.example.ailifelegacy.api.user_case.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import org.example.ailifelegacy.api.life_legacy_toc.entity.LifeLegacyToc;
import org.example.ailifelegacy.api.user.entity.User;

@Entity
@Table(name = "user_cases")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String caseName;

    @Column(length = 255)
    private String description; // null 허용은 기본값

    @ManyToMany
    @JoinTable(
        name = "case_toc_mapping",
        joinColumns = @JoinColumn(name = "user_case_id"),
        inverseJoinColumns = @JoinColumn(name = "toc_id")
    )
    private List<LifeLegacyToc> tocs;

    @OneToMany(mappedBy = "userCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;
}
