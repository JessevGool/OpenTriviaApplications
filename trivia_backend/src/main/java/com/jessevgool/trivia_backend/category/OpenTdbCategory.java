package com.jessevgool.trivia_backend.category;

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
public class OpenTdbCategory {
    @JsonProperty()
    int id;

    @JsonProperty()
    String name;
}
