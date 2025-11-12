package com.jessevgool.trivia_backend.question;

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
public class Question {
    String id;
    String question;
    String type;
    String[] answers;
}
