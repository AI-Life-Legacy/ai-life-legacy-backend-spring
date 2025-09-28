package org.example.ailifelegacy.api.user_case_toc.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.example.ailifelegacy.api.life_legacy_toc.entity.LifeLegacyToc;
import org.example.ailifelegacy.api.user_case.entity.UserCase;

@Entity
@Table(name = "case_toc_mapping")
@Getter
@Setter
public class UserCaseTocMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_case_id", nullable = false)
    private UserCase userCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toc_id", nullable = false)
    private LifeLegacyToc toc;
}
