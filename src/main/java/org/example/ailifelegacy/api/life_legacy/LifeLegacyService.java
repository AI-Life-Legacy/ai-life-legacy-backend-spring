package org.example.ailifelegacy.api.life_legacy;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.ailifelegacy.api.life_legacy.dto.request.SaveUserAnswerDto;
import org.example.ailifelegacy.api.life_legacy.dto.response.GetUserQuestionsDto;
import org.example.ailifelegacy.api.life_legacy.entity.LifeLegacyAnswer;
import org.example.ailifelegacy.api.life_legacy_question.LifeLegacyQuestionRepository;
import org.example.ailifelegacy.api.life_legacy_question.entity.LifeLegacyQuestion;
import org.example.ailifelegacy.api.life_legacy_toc.LifeLegacyTocRepository;
import org.example.ailifelegacy.api.life_legacy_toc.entity.LifeLegacyToc;
import org.example.ailifelegacy.api.user.UserRepository;
import org.example.ailifelegacy.api.user.dto.request.SaveUserIntroDto;
import org.example.ailifelegacy.api.user.entity.User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LifeLegacyService {
    private final LifeLegacyRepository lifeLegacyRepository;
    private final LifeLegacyQuestionRepository lifeLegacyQuestionRepository;
    private final LifeLegacyTocRepository lifeLegacyTocRepository;
    private final UserRepository userRepository;


    @Transactional
    public List<GetUserQuestionsDto> getQuestions(Long tocId, UUID uuid) {
        // 1. tocId로 질문 가져오기 (questions까지 fetch join)
        LifeLegacyToc toc = lifeLegacyTocRepository.findByIdWithQuestions(tocId)
            .orElseThrow(() -> new IllegalArgumentException("toc not found: " + tocId));

        List<LifeLegacyQuestion> tocQuestions = toc.getQuestions();

        // 2. 유저가 작성한 모든 답변 가져오기
        User user = userRepository.findByUuid(uuid)
            .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: " + uuid));

        List<LifeLegacyAnswer> userAnswers = lifeLegacyRepository.findByUser(user);

        // 3. 유저가 답변한 questionId 찾기
        Set<Long> answeredQuestionIds = userAnswers.stream()
            .map(answer -> answer.getQuestion().getId())
            .collect(Collectors.toSet());

        // 4. 유저가 작성하지 않은 질문만 필터링 + DTO 변환
        return tocQuestions.stream()
            .filter(q -> !answeredQuestionIds.contains(q.getId()))
            .map(q -> new GetUserQuestionsDto(q.getId(), q.getQuestionText()))
            .toList();
    }

    @Transactional
    public void saveAnswer(UUID uuid, Long questionId, SaveUserAnswerDto saveUserAnswerDto) {
        String answerText = saveUserAnswerDto.getAnswerText();

        User user = userRepository.findByUuid(uuid)
            .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: " + uuid));

        LifeLegacyQuestion question = lifeLegacyQuestionRepository.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Question not found with id: " + questionId));

        boolean exists = lifeLegacyRepository.findOneByUserAndQuestion(user, question).isPresent();
        if (exists) throw new IllegalStateException("Answer already exists");

        LifeLegacyAnswer userAnswer = LifeLegacyAnswer.builder()
            .answerText(answerText)
            .question(question)
            .user(user)
            .build();
        lifeLegacyRepository.save(userAnswer);
    }
}
