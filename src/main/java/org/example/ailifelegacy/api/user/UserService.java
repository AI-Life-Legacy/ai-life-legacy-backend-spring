package org.example.ailifelegacy.api.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.life_legacy.LifeLegacyRepository;
import org.example.ailifelegacy.api.life_legacy.dto.request.UpdateUserAnswerDto;
import org.example.ailifelegacy.api.life_legacy.entity.LifeLegacyAnswer;
import org.example.ailifelegacy.api.life_legacy_question.LifeLegacyQuestionRepository;
import org.example.ailifelegacy.api.life_legacy_question.entity.LifeLegacyQuestion;
import org.example.ailifelegacy.api.life_legacy_toc.LifeLegacyTocRepository;
import org.example.ailifelegacy.api.life_legacy_toc.entity.LifeLegacyToc;
import org.example.ailifelegacy.api.user.dto.request.SaveUserIntroDto;
import org.example.ailifelegacy.api.user.dto.response.GetUserAnswerResponseDto;
import org.example.ailifelegacy.api.user.dto.response.GetUserQuestionResponseDto;
import org.example.ailifelegacy.api.user.dto.response.GetUserTocResponseDto;
import org.example.ailifelegacy.api.user.dto.response.GetUserTocWithQuestionsResponseDto;
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
    private final LifeLegacyTocRepository lifeLegacyTocRepository;
    private final LifeLegacyRepository lifeLegacyRepository;
    private final LifeLegacyQuestionRepository lifeLegacyQuestionRepository;

    @Transactional
    public void saveUserIntro(UUID uuid, SaveUserIntroDto saveUserIntroDto) {
        String userIntroText = saveUserIntroDto.getUserIntro();
        User user = userRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

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

    @Transactional
    public List<GetUserTocResponseDto> getUserToc(UUID uuid) {
        User user = userRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserCase userCase = user.getUserCase();
        List<LifeLegacyToc> userToc = lifeLegacyTocRepository.findByUserCases(userCase);

        return userToc.stream()
            .map(toc -> new GetUserTocResponseDto(toc.getId(), toc.getTitle(), toc.getOrderIndex()))
            .toList();
    }

    public List<GetUserTocWithQuestionsResponseDto> getUserTocAndQuestions(UUID uuid) {
        User user = userRepository.findByUuid(uuid)
            .orElseThrow(() -> {
                System.out.println("User not found with uuid: " + uuid);
                return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
            });

        UserCase userCase = user.getUserCase();
        List<LifeLegacyToc> userToc = lifeLegacyTocRepository.findByUserCases(userCase);

        return userToc.stream()
            .map(toc -> new GetUserTocWithQuestionsResponseDto(
                toc.getId(),
                toc.getTitle(),
                toc.getOrderIndex(),
                toc.getQuestions().stream()
                    .map(q -> new GetUserQuestionResponseDto(
                        q.getId(),
                        q.getQuestionText(),
                        q.getOrderIndex()
                    ))
                    .toList()
            ))
            .toList();
    }

    public GetUserAnswerResponseDto getUserAnswer(Long questionId, UUID uuid) {
        User user = userRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LifeLegacyQuestion question =  lifeLegacyQuestionRepository.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + questionId));

        Optional<LifeLegacyAnswer> userAnswer = lifeLegacyRepository.findOneByUserAndQuestion(user, question);

        return userAnswer
            .map(answer -> new GetUserAnswerResponseDto(
                questionId,
                answer.getAnswerText()
            ))
            .orElseThrow(() -> new EntityNotFoundException("해당 질문의 답변을 찾을 수 없습니다."));
    }

    @Transactional
    public void updateUserAnswer(Long answerId, UUID uuid, UpdateUserAnswerDto updateUserAnswerDto) {
        String newAnswerText = updateUserAnswerDto.getNewAnswerText();

        User user = userRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LifeLegacyAnswer userAnswer = lifeLegacyRepository.findOneByUserAndId(user, answerId)
            .orElseThrow(() -> new EntityNotFoundException("유저의 답변을 찾을 수 없습니다."));

        userAnswer.setAnswerText(newAnswerText);
        lifeLegacyRepository.save(userAnswer);
    }
}
