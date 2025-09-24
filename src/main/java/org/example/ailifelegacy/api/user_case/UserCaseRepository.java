package org.example.ailifelegacy.api.user_case;

import org.example.ailifelegacy.api.user_case.entity.UserCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCaseRepository extends JpaRepository<UserCase, Long> {
    UserCase findUserCaseByCaseName(String caseName);
}
