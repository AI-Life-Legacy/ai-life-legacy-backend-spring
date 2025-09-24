package org.example.ailifelegacy.api.user_intro;

import java.util.Optional;
import java.util.UUID;
import org.example.ailifelegacy.api.user.entity.User;
import org.example.ailifelegacy.api.user_intro.entity.UserIntro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserIntroRepository extends JpaRepository<UserIntro, UUID> {

    Optional<UserIntro> findByUser(User user);
}
