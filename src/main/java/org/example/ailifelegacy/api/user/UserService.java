package org.example.ailifelegacy.api.user;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.entity.User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User findByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid)
            .orElseThrow(() -> {
                System.out.println("User not found with uuid: " + uuid);
                return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
            });
    }
}
