package org.example.ailifelegacy.api.user;

import java.util.Optional;
import java.util.UUID;
import org.example.ailifelegacy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUuid(UUID uuid);
}
