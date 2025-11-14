/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jessevgool.trivia_backend.token;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jessevgool.trivia_backend.service.TriviaService;

/**
 *
 * @author Jesse van Gool
 */
@RestController
public class TokenController {

    private final TriviaService triviaService;

    public TokenController(TriviaService triviaService) {
        this.triviaService = triviaService;
    }

    /**
     * Fetches a new OpenTdb session token.
     * @return An OpenTdbToken object representing the new session token.
     */
    @GetMapping("/token")
    public OpenTdbToken getToken() {
        return triviaService.fetchToken();
    }
}
