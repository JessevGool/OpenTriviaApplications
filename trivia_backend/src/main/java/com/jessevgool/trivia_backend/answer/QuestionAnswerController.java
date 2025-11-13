/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jessevgool.trivia_backend.answer;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jessevgool.trivia_backend.service.TriviaService;

/**
 *
 * @author Jesse van Gool
 */
@RestController
public class QuestionAnswerController {
    private final TriviaService triviaService;

    public QuestionAnswerController(TriviaService triviaService) {
        this.triviaService = triviaService;
    }

    @PostMapping("submit")
    public List<QuestionAnswer> postMethodName(
        @RequestParam String token,
        @RequestBody List<QuestionAnswer> answers) {
        answers.forEach(
                qa -> qa.setCorrect(triviaService.checkQuestionAnswer(token, qa.getQuestionId(), qa.getAnswer())));
        return answers;
    }

}
