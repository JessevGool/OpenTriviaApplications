/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jessevgool.trivia_backend.token;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
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
public class OpenTdbToken {
    @JsonProperty("response_code")
    int responseCode;
    @JsonProperty("response_message")
    String responseMessage;
    String token;
}
