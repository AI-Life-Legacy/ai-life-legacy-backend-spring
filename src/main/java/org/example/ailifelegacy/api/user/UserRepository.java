package org.example.ailifelegacy.api.user;

import java.util.Optional;
import java.util.UUID;
import org.example.ailifelegacy.api.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {}
