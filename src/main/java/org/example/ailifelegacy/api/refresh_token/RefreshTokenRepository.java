package org.example.ailifelegacy.api.refresh_token;

import java.util.Optional;
import org.example.ailifelegacy.api.refresh_token.entity.RefreshToken;
import org.example.ailifelegacy.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);
}
