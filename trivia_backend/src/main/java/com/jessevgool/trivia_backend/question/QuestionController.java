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

    /**
     * Fetches trivia questions from the trivia service based on the provided parameters. Handles various exceptions related to the trivia provider.
     * 
     * @param amount The `amount` parameter in the `getQuestions` method specifies the number of trivia questions to fetch. 
     * It is a query parameter that has a default value of 5, meaning that if the client does not provide a value for this parameter,
     * the method will fetch 5 questions by default.
     * @param category The `category` parameter in the `getQuestions` method is used to specify the category of trivia questions to fetch.
     * @param difficulty The `difficulty` parameter in the `getQuestions` method is used to specify the difficulty level of the trivia questions to be fetched.
     * @param type The `type` parameter in the `getQuestions` method is used to specify the type of trivia questions to be fetched.
     * @param token The `token` parameter in the `getQuestions` method is an optional query parameter that represents a session token
     * @return A ResponseEntity containing the fetched questions or an error message with the appropriate HTTP status code.
     */
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
