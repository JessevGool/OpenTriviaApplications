/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jessevgool.trivia_backend.answer;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

/**
 *
 * @author Jesse van Gool
 */
@Jacksonized
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswer {
    UUID questionId;
    String answer;
    @Builder.Default
    boolean correct = false;
}
