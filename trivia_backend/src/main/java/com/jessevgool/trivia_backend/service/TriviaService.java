
package com.jessevgool.trivia_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.jessevgool.trivia_backend.question.OpenTdbQuestion;
import com.jessevgool.trivia_backend.question.OpenTdbQuestionResponse;
import com.jessevgool.trivia_backend.question.Question;

/**
 *
 * @author Jesse van Gool
 */
@Service
public class TriviaService {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://opentdb.com")
            .build();

    public Question[] fetchQuestions(int amount) {
        OpenTdbQuestionResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api.php")
                        .queryParam("amount", amount)
                        .build())
                .retrieve()
                .body(OpenTdbQuestionResponse.class);

        if (response != null && response.getResults() != null) {
            OpenTdbQuestion[] openTdbQuestions = response.getResults();
            Question[] questions = new Question[openTdbQuestions.length];

            for (int i = 0; i < openTdbQuestions.length; i++) {
                OpenTdbQuestion otdbQuestion = openTdbQuestions[i];
                String[] allAnswers = new String[otdbQuestion.getIncorrectAnswers().length + 1];
                System.arraycopy(otdbQuestion.getIncorrectAnswers(), 0, allAnswers, 0, otdbQuestion.getIncorrectAnswers().length);
                allAnswers[allAnswers.length - 1] = otdbQuestion.getCorrectAnswer();
                questions[i] = Question.builder()
                        .id(String.valueOf(i + 1))
                        .question(otdbQuestion.getQuestion())
                        .type(otdbQuestion.getType())
                        .answers(allAnswers)
                        .build();
            }

            return questions;
        }

        return new Question[0];
    }
}
