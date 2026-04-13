package com.example.content_calendar.SecurityConfig;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@EnableScheduling
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ConcurrentMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();

    @Value("${rate-limit.requests-per-minute:20}")
    private int requestsPerMinute;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String clientIp = resolveClientIp(request);
        AtomicInteger count = requestCounts.computeIfAbsent(clientIp, k -> new AtomicInteger(0));

        if (count.incrementAndGet() > requestsPerMinute) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", "60");
            response.getWriter().write(
                    "{\"statusCode\":429,\"message\":\"Rate limit exceeded. Try again later.\"}"
            );
            return;
        }

        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(Math.max(0, requestsPerMinute - count.get())));
        filterChain.doFilter(request, response);
    }

    // Reset all counters every minute
    @Scheduled(fixedRate = 60000)
    public void resetCounts() {
        requestCounts.clear();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
