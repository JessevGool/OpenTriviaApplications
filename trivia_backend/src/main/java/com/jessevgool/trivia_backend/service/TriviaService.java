
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

        /**
         * A concurrent map to store correct answers for each session token.
         * The outer map's key is the session token, and the inner map's key is the
         * question UUID with its correct answer as value.
         * normally, this would be stored in a database or cache.
         */
        private final Map<String, Map<UUID, String>> answersBySession = new ConcurrentHashMap<>();

        /**
         * Fetches trivia questions from the OpenTdb API and stores the correct answers
         * for the session.
         * 
         * @param amount     The number of questions to fetch.
         * @param category   The category of questions to fetch.
         * @param difficulty The difficulty level of questions to fetch.
         * @param type       The type of questions to fetch.
         * @param token      The session token for the trivia session.
         * @return An array of Question objects representing the fetched trivia
         *         questions.
         */
        public Question[] fetchQuestions(int amount, Integer category, String difficulty, String type, String token) {
                OpenTdbQuestionResponse response;

                response = fetchQuestionsRaw(amount, category, difficulty, type, token);

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

        /**
         * Fetches raw questions from the OpenTdb API.
         * 
         * @param amount     The number of questions to fetch.
         * @param category   The category of questions to fetch.
         * @param difficulty The difficulty level of questions to fetch.
         * @param type       The type of questions to fetch.
         * @param token      The session token for the trivia session.
         * @return An OpenTdbQuestionResponse containing the fetched questions.
         */
        private OpenTdbQuestionResponse fetchQuestionsRaw(int amount, Integer category, String difficulty, String type,
                        String token) {
                try {
                        return restClient.get()
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
        }

        /**
         * Checks if the provided answer for a question is correct.
         * 
         * @param token      The session token identifying the trivia session.
         * @param questionId The UUID of the question being answered.
         * @param answer     The answer provided by the user.
         * @return true if the answer is correct, false otherwise.
         */
        public boolean checkQuestionAnswer(String token, UUID questionId, String answer) {
                Map<UUID, String> answersForSession = answersBySession.get(token);
                if (answersForSession == null) {
                        return false;
                }

                String correctAnswer = answersForSession.get(questionId);
                return correctAnswer != null && correctAnswer.equalsIgnoreCase(answer);
        }

        /**
         * Ends a trivia session by removing its stored answers.
         * 
         * @param token The session token identifying the trivia session to be ended.
         */
        public void endSession(String token) {
                answersBySession.remove(token);
        }

        /**
         * Fetches a new session token from the OpenTdb API.
         * 
         * @return An OpenTdbToken object representing the new session token.
         */
        public OpenTdbToken fetchToken() {
                return restClient.get()
                                .uri("/api_token.php?command=request")
                                .retrieve()
                                .body(OpenTdbToken.class);
        }

        /**
         * Wraps a value in an Optional.
         * 
         * @param value The value to be wrapped in an Optional.
         * @return An Optional containing the provided value, or an empty Optional if
         *         the value is null.
         */
        private static <T> Optional<T> opt(T value) {
                return Optional.ofNullable(value);
        }

        /**
         * Converts an OpenTdbQuestion to a Question object, shuffling the answers and
         * storing the correct answer.
         * 
         * @param src               The `src` parameter is an instance of the
         *                          `OpenTdbQuestion` class, which represents a trivia
         *                          question
         * @param answersForSession A map that stores the correct answers for the
         *                          current session, using UUIDs as keys.
         * @return A Question object with shuffled answers and a unique ID.
         */
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

        /**
         * Checks the response code from the OpenTDB API and throws appropriate
         * exceptions based on the code.
         * 
         * @param code The response code from the OpenTDB API.
         */
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
