package org.example.ailifelegacy.api.user_case;

import java.util.Optional;
import org.example.ailifelegacy.api.user_case.entity.UserCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCaseRepository extends JpaRepository<UserCase, Long> {
    Optional<UserCase> findUserCaseByCaseName(String caseName);
}
