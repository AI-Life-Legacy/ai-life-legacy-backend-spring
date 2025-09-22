package org.example.ailifelegacy.api.auth_identity;

import java.util.List;
import org.example.ailifelegacy.api.auth_identity.entity.AuthIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthIdentityRepository extends JpaRepository<AuthIdentity, Long> {
    AuthIdentity findByProviderUuid(String providerUuid);
}
