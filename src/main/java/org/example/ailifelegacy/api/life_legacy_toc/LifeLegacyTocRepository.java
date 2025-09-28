package org.example.ailifelegacy.api.life_legacy_toc;

import java.util.Optional;
import org.example.ailifelegacy.api.life_legacy_toc.entity.LifeLegacyToc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LifeLegacyTocRepository extends JpaRepository <LifeLegacyToc, Long> {
    @Query("SELECT DISTINCT t FROM LifeLegacyToc t LEFT JOIN FETCH t.questions WHERE t.id = :tocId")
    Optional<LifeLegacyToc> findByIdWithQuestions(@Param("tocId") Long tocId);
}
