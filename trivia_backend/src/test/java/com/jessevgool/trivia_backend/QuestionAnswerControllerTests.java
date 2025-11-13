/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package com.jessevgool.trivia_backend;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jessevgool.trivia_backend.answer.QuestionAnswer;
import com.jessevgool.trivia_backend.answer.QuestionAnswerController;
import com.jessevgool.trivia_backend.service.TriviaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 *
 * @author Jesse van Gool
 */

@WebMvcTest(controllers = QuestionAnswerController.class)
public class QuestionAnswerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TriviaService triviaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void submitAnswers_SetsCorrectFlagBasedOnService() throws Exception {

        String token = "test-token";
        UUID questionId = UUID.randomUUID();

        QuestionAnswer qa = new QuestionAnswer();
        qa.setQuestionId(questionId);
        qa.setAnswer("Paris");

        List<QuestionAnswer> payload = List.of(qa);

        when(triviaService.checkQuestionAnswer(token, questionId, "Paris"))
                .thenReturn(true);

        mockMvc.perform(post("/submit")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correct").value(true))
                .andExpect(jsonPath("$[0].answer").value("Paris"));

        verify(triviaService).checkQuestionAnswer(token, questionId, "Paris");
    }

    @Test
    void submitAnswers_WhenServiceReturnsFalse_CorrectIsFalse() throws Exception {
        String token = "test-token";
        UUID questionId = UUID.randomUUID();

        QuestionAnswer qa = new QuestionAnswer();
        qa.setQuestionId(questionId);
        qa.setAnswer("London");

        List<QuestionAnswer> payload = List.of(qa);

        when(triviaService.checkQuestionAnswer(token, questionId, "London"))
                .thenReturn(false);

        mockMvc.perform(post("/submit")
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correct").value(false));
    }

    
}