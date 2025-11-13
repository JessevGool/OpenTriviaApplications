/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package com.jessevgool.trivia_backend;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jessevgool.trivia_backend.service.OpenTdbMetaService;

/**
 *
 * @author Jesse van Gool
 */
@SpringBootTest
@AutoConfigureMockMvc
public class OpenTdbMetaServiceTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private OpenTdbMetaService metaService;

  @Test
  void contextLoads() throws Exception {
    assertThat(mockMvc).isNotNull();
    assertThat(metaService).isNotNull();
  }

  @Test
  void getCategories_ReturnsOk() throws Exception {
    mockMvc.perform(get("/categories"))
        .andExpect(status().isOk());
  }

   @Test
  void getCategories_MultipleRequests_ReturnsOk() throws Exception {
    for (int i = 0; i < 5; i++) {
      mockMvc.perform(get("/categories"))
          .andExpect(status().isOk());
    }
  }
}