
package com.jessevgool.trivia_backend.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.jessevgool.trivia_backend.question.OpenTdbQuestion;
import com.jessevgool.trivia_backend.question.OpenTdbQuestionResponse;
import com.jessevgool.trivia_backend.question.Question;
import com.jessevgool.trivia_backend.token.OpenTdbToken;

/**
 *
 * @author Jesse van Gool
 */
@Service
public class TriviaService {

        private final RestClient restClient = RestClient.builder()
                        .baseUrl("https://opentdb.com")
                        .build();

        HashMap<UUID, String> questionAnswers = new HashMap<>();

        public Question[] fetchQuestions(int amount, Integer category, String difficulty, String type, String token) {
                questionAnswers.clear();
                OpenTdbQuestionResponse response = restClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/api.php")
                                                .queryParam("amount", amount)
                                                .queryParamIfPresent("category",
                                                                category != null ? java.util.Optional.of(category)
                                                                                : java.util.Optional.empty())
                                                .queryParamIfPresent("difficulty",
                                                                difficulty != null ? java.util.Optional.of(difficulty)
                                                                                : java.util.Optional.empty())
                                                .queryParamIfPresent("type",
                                                                type != null ? java.util.Optional.of(type)
                                                                                : java.util.Optional.empty())
                                                .queryParamIfPresent("token",
                                                                token != null ? java.util.Optional.of(token)
                                                                                : java.util.Optional.empty())
                                                .build())
                                .retrieve()
                                .body(OpenTdbQuestionResponse.class);

                if (response != null && response.getResults() != null) {
                        OpenTdbQuestion[] openTdbQuestions = response.getResults();
                        Question[] questions = new Question[openTdbQuestions.length];

                        for (int i = 0; i < openTdbQuestions.length; i++) {
                                OpenTdbQuestion otdbQuestion = openTdbQuestions[i];
                                String[] allAnswers = new String[otdbQuestion.getIncorrectAnswers().length + 1];
                                System.arraycopy(otdbQuestion.getIncorrectAnswers(), 0, allAnswers, 0,
                                                otdbQuestion.getIncorrectAnswers().length);
                                allAnswers[allAnswers.length - 1] = otdbQuestion.getCorrectAnswer();
                                questions[i] = Question.builder()
                                                .id(java.util.UUID.randomUUID())
                                                .question(otdbQuestion.getQuestion())
                                                .type(otdbQuestion.getType())
                                                .answers(allAnswers)
                                                .build();
                                questionAnswers.put(questions[i].getId(), otdbQuestion.getCorrectAnswer());
                        }

                        return questions;
                }

                return new Question[0];
        }

        public boolean checkQuestionAnswer(UUID questionId, String answer) {
                String correctAnswer = questionAnswers.get(questionId);
                return correctAnswer != null && correctAnswer.equals(answer);
        }

        public OpenTdbToken fetchToken() {
                return restClient.get()
                                .uri("/api_token.php?command=request")
                                .retrieve()
                                .body(OpenTdbToken.class);
        }
}
