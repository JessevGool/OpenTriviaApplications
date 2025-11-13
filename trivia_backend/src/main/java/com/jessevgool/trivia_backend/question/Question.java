package com.jessevgool.trivia_backend.question;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    UUID id;
    String question;
    String type;
    String[] answers;

    
}
