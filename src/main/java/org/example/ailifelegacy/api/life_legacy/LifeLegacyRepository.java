package org.example.ailifelegacy.api.life_legacy;

import java.util.List;
import java.util.Optional;
import org.example.ailifelegacy.api.life_legacy.entity.LifeLegacyAnswer;
import org.example.ailifelegacy.api.life_legacy_question.entity.LifeLegacyQuestion;
import org.example.ailifelegacy.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LifeLegacyRepository extends JpaRepository <LifeLegacyAnswer, Long> {

    List<LifeLegacyAnswer> findByUser(User user);

    Optional<LifeLegacyAnswer> findOneByUserAndId(User user, Long answerId);

    Optional<LifeLegacyAnswer>  findOneByUserAndQuestion(User user, LifeLegacyQuestion question);
}
