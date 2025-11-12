package com.jessevgool.trivia_backend.category;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jessevgool.trivia_backend.service.OpenTdbMetaService;

/**
 *
 * @author Jesse van Gool
 */
@RestController
public class OpenTdbCategoryController {
    private final OpenTdbMetaService openTdbMetaService;

    public OpenTdbCategoryController(OpenTdbMetaService openTdbMetaService) {
        this.openTdbMetaService = openTdbMetaService;
    }

    @GetMapping("/categories")
    public Object getCategories() {
        return openTdbMetaService.fetchTriviaCategories();
    
    }}
