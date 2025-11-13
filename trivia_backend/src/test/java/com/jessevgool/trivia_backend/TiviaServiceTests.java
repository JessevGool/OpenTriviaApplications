/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package com.jessevgool.trivia_backend;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jessevgool.trivia_backend.exceptions.ExceptionEnums;
import com.jessevgool.trivia_backend.service.TriviaService;
import com.jessevgool.trivia_backend.token.OpenTdbToken;

/**
 *
 * @author Jesse van Gool
 */
@SpringBootTest
@AutoConfigureMockMvc
class TiviaServiceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TriviaService triviaService;

    @Test
    void contextLoads() throws Exception {
        assertThat(mockMvc).isNotNull();
        assertThat(triviaService).isNotNull();
    }

    private OpenTdbToken token;

    @BeforeEach
    void setUp() {
        token = triviaService.fetchToken();
    }

    @AfterEach
    void afterEach() throws InterruptedException {
        Thread.sleep(5001); // To avoid rate limiting during tests
    }

    @Test
    void getQuestions_WithoutParameters_UsesDefaults() throws Exception {
        mockMvc.perform(get("/questions")
                .param("token", token.getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getQuestions_WithParameters_ReturnsOk() throws Exception {
        mockMvc.perform(get("/questions")
                .param("amount", "5")
                .param("category", "9")
                .param("difficulty", "medium")
                .param("type", "multiple")
                .param("token", token.getToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getQuestions_MultipleRequests_ReturnsError() throws Exception {
        //Should be ok
        mockMvc.perform(get("/questions")
                .param("token", token.getToken()))
                .andExpect(status().isOk());
        //Should throw 429 rate limit error
        mockMvc.perform(get("/questions")
                .param("token", token.getToken()))
                .andExpect(status().is(ExceptionEnums.OPEN_TDB_RATE_LIMIT.getCode()));
    }

    @Test
void getQuestions_OnlyAmountAndToken_ReturnsFiveQuestions() throws Exception {
    mockMvc.perform(get("/questions")
            .param("amount", "5")
            .param("token", token.getToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(5));
}


}