/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.jessevgool.trivia_backend.config.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Jesse van Gool
 */
@Component
public class QuestionsRateLimitFilter extends OncePerRequestFilter {

    private final AtomicLong lastCallTime = new AtomicLong(0);
    private static final long WINDOW_MS = 5_000L;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException, ServletException {

        // Let preflight requests pass
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            addCorsHeaders(request, response);
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        if ("/questions".equals(path)) {
            long now = System.currentTimeMillis();

            while (true) {
                long previous = lastCallTime.get();
                long diff = now - previous;

                if (diff < WINDOW_MS) {
                  
                    addCorsHeaders(request, response);

                    response.setStatus(429);
                    response.setContentType("text/plain");
                    response.getWriter().write("You can only call /questions once every 5 seconds.");
                    return;
                }

                if (lastCallTime.compareAndSet(previous, now)) {
                    break;
                }
            }
        }

        addCorsHeaders(request, response);
        filterChain.doFilter(request, response);
    }

    private void addCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (origin != null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
        }

        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        response.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");
    }
}

