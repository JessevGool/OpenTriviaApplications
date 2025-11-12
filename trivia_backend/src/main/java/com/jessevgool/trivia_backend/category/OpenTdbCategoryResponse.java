package com.jessevgool.trivia_backend.category;

import java.util.List;

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
public class OpenTdbCategoryResponse {
    @JsonProperty("trivia_categories")
    List<OpenTdbCategory> categories;
}
