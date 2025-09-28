package org.example.ailifelegacy.api.user_case_toc;

import java.util.List;
import org.example.ailifelegacy.api.user_case.entity.UserCase;
import org.example.ailifelegacy.api.user_case_toc.entity.UserCaseTocMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCaseTocRepository extends JpaRepository<UserCaseTocMapping, Long> {
    List<UserCaseTocMapping> findAllByUserCase(UserCase userCase);
}
