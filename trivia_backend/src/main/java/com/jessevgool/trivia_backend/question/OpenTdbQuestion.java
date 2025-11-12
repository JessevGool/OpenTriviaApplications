package com.jessevgool.trivia_backend.question;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/**
 *
 * @author Jesse van Gool
 */
@Jacksonized
@Builder
@Getter
@Setter
public class OpenTdbQuestion {
    String type;
    String difficulty;
    String category;
    String question;
    @JsonProperty("correct_answer")
    String correctAnswer;
    @JsonProperty("incorrect_answers")
    String[] incorrectAnswers;
}
