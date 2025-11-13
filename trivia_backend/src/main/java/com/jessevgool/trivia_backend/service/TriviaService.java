
package com.jessevgool.trivia_backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.jessevgool.trivia_backend.exceptions.OpenTdbException;
import com.jessevgool.trivia_backend.exceptions.OpenTdbRateLimitException;
import com.jessevgool.trivia_backend.exceptions.OpenTdbTokenException;
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

        private final Map<String, Map<UUID, String>> answersBySession = new ConcurrentHashMap<>();

        public Question[] fetchQuestions(int amount, Integer category, String difficulty, String type, String token) {
                OpenTdbQuestionResponse response;
                try {
                        response = restClient.get()
                                        .uri(uriBuilder -> uriBuilder
                                                        .path("/api.php")
                                                        .queryParam("amount", amount)
                                                        .queryParamIfPresent("category",
                                                                        opt(category))
                                                        .queryParamIfPresent("difficulty",
                                                                        opt(difficulty))
                                                        .queryParamIfPresent("type",
                                                                        opt(type))
                                                        .queryParamIfPresent("token",
                                                                        opt(token))
                                                        .build())
                                        .retrieve()
                                        .body(OpenTdbQuestionResponse.class);
                } catch (HttpClientErrorException e) {
                        if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                                throw new OpenTdbRateLimitException("OpenTDB rate limit reached", e);
                        }
                        throw e;
                }
                if (response == null) {
                        throw new OpenTdbException("Invalid response from OpenTDB");
                }
                checkResponseCode(response.getResponseCode());

                OpenTdbQuestion[] openTdbQuestions = response.getResults();
                Question[] questions = new Question[openTdbQuestions.length];

                Map<UUID, String> answersForSession = answersBySession.computeIfAbsent(token,
                                id -> new ConcurrentHashMap<>());

                for (int i = 0; i < openTdbQuestions.length; i++) {
                        questions[i] = toQuestion(openTdbQuestions[i], answersForSession);
                }
                return questions;
        }

        public boolean checkQuestionAnswer(String token, UUID questionId, String answer) {
                Map<UUID, String> answersForSession = answersBySession.get(token);
                if (answersForSession == null) {
                        return false;
                }

                String correctAnswer = answersForSession.get(questionId);
                return correctAnswer != null && correctAnswer.equalsIgnoreCase(answer);
        }

        public void endSession(String token) {
                answersBySession.remove(token);
        }

        public OpenTdbToken fetchToken() {
                return restClient.get()
                                .uri("/api_token.php?command=request")
                                .retrieve()
                                .body(OpenTdbToken.class);
        }

        private static <T> Optional<T> opt(T value) {
                return Optional.ofNullable(value);
        }

        private Question toQuestion(OpenTdbQuestion src, Map<UUID, String> answersForSession) {
                List<String> allAnswers = new ArrayList<>();
                Collections.addAll(allAnswers, src.getIncorrectAnswers());
                allAnswers.add(src.getCorrectAnswer());
                Collections.shuffle(allAnswers);

                UUID id = UUID.randomUUID();
                answersForSession.put(id, src.getCorrectAnswer());

                return Question.builder()
                                .id(id)
                                .question(src.getQuestion())
                                .type(src.getType())
                                .answers(allAnswers.toArray(String[]::new))
                                .build();
        }

        private void checkResponseCode(int code) {
                switch (code) {
                        case 0, 1 -> {
                                break;
                        }
                        case 2 -> throw new OpenTdbException("Invalid Parameter: Contains an invalid parameter.");
                        case 3 -> throw new OpenTdbTokenException("Token Not Found: Session Token does not exist.");
                        case 4 -> throw new OpenTdbTokenException(
                                        "Token Empty: Session Token has returned all possible questions for the specified query. Resetting the Token is necessary.");
                        case 5 -> throw new OpenTdbException("Invalid Session: The Session Token provided is invalid.");
                        default -> throw new OpenTdbException("Unknown response code from OpenTDB: " + code);
                }
        }
}
