package org.example.ailifelegacy.api.user;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.user.dto.request.SaveUserIntroDto;
import org.example.ailifelegacy.api.user.entity.User;
import org.example.ailifelegacy.api.user_case.UserCaseRepository;
import org.example.ailifelegacy.api.user_case.entity.UserCase;
import org.example.ailifelegacy.api.user_intro.UserIntroRepository;
import org.example.ailifelegacy.api.user_intro.entity.UserIntro;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserIntroRepository userIntroRepository;
    private final UserCaseRepository userCaseRepository;

    public void saveUserIntro(UUID uuid, SaveUserIntroDto saveUserIntroDto) {
        String userIntroText = saveUserIntroDto.getUserIntro();
        User user = userRepository.findByUuid(uuid)
            .orElseThrow(() -> {
                System.out.println("User not found with uuid: " + uuid);
                return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
            });

        boolean exits = userIntroRepository.findByUser(user).isPresent();
        if (exits) throw new IllegalStateException("이미 해당 유저의 자기소개가 존재합니다.");

        // UserCase AI 서버한테 받아오기
        String caseName = "case1";
        UserCase userCase = userCaseRepository.findUserCaseByCaseName(caseName);

        user.setUserCase(userCase);
        userRepository.save(user);

        UserIntro userIntro = UserIntro.builder()
            .introText(userIntroText)
            .user(user)
            .build();
        userIntroRepository.save(userIntro);
    }
}
