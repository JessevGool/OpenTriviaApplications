package com.jessevgool.trivia_backend.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.jessevgool.trivia_backend.category.OpenTdbCategory;
import com.jessevgool.trivia_backend.category.OpenTdbCategoryResponse;

/**
 *
 * @author Jesse van Gool
 */
@Service
public class OpenTdbMetaService {
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://opentdb.com")
            .build();

    @Cacheable("categories")
    public List<OpenTdbCategory> fetchTriviaCategories() {
        OpenTdbCategoryResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api_category.php")
                        .build())
                .retrieve()
                .body(OpenTdbCategoryResponse.class);

        return response != null ? response.getCategories() : List.of();
    }

    @CacheEvict(value = "categories", allEntries = true)
    public void evictCategoriesCache() {
    }
}
