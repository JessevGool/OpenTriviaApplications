/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jessevgool.trivia_backend.question;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jessevgool.trivia_backend.service.TriviaService;

/**
 *
 * @author Jesse van Gool
 */
@RestController
public class QuestionController {
    private final TriviaService triviaService;

    public QuestionController(TriviaService triviaService) {
        this.triviaService = triviaService;
    }

    @GetMapping("/questions")
    public Object getQuestions(
            @RequestParam(defaultValue = "5") int amount,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String token) {
        return triviaService.fetchQuestions(amount, category, difficulty, type,token);
    }
}
