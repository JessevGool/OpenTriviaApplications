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
public class OpenTdbQuestionResponse {
    @JsonProperty("response_code")
    private int responseCode;
    private OpenTdbQuestion[] results;
}
