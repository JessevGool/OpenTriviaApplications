/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jessevgool.trivia_backend.question;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jessevgool.trivia_backend.exceptions.ExceptionEnums;
import com.jessevgool.trivia_backend.exceptions.OpenTdbException;
import com.jessevgool.trivia_backend.exceptions.OpenTdbRateLimitException;
import com.jessevgool.trivia_backend.exceptions.OpenTdbTokenException;
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
        try {
            Question[] questions = triviaService.fetchQuestions(amount, category, difficulty, type, token);
            return ResponseEntity.ok(questions);
        } catch (OpenTdbRateLimitException e) {
            return ResponseEntity.status(ExceptionEnums.OPEN_TDB_RATE_LIMIT.getCode()).body("Trivia API is temporarily rate limited. Please try again soon.");
        } catch (OpenTdbTokenException e) {
            return ResponseEntity.status(ExceptionEnums.OPEN_TDB_TOKEN_EXCEPTION.getCode()).body("Your trivia session expired. Please start a new game.");
        } catch (OpenTdbException e) {
            return ResponseEntity.status(ExceptionEnums.OPEN_TDB_EXCEPTION.getCode()).body("Trivia provider error: " + e.getMessage());
        }
    }
}
