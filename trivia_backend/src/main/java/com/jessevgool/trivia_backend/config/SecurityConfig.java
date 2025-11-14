package com.jessevgool.trivia_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.jessevgool.trivia_backend.config.filter.QuestionsRateLimitFilter;

/**
 *
 * @author Jesse van Gool
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final QuestionsRateLimitFilter questionsRateLimitFilter;

    public SecurityConfig(QuestionsRateLimitFilter questionsRateLimitFilter) {
        this.questionsRateLimitFilter = questionsRateLimitFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/questions").permitAll()
                .anyRequest().permitAll()
            )
            .addFilterAfter(questionsRateLimitFilter, org.springframework.web.filter.CorsFilter.class);

        return http.build();
    }
}
