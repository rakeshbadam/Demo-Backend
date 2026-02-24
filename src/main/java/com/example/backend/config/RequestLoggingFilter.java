package com.example.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String TRACE_ID = "traceId";

    private static final long SLOW_REQUEST_THRESHOLD_MS = 1000;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // 🔹 Exclude noisy paths
        if (shouldSkipLogging(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();

        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);

        String method = request.getMethod();
        String clientIp = request.getRemoteAddr();

        try {

            log.info("event=request_start method={} uri={} ip={}",
                    method, uri, clientIp);

            filterChain.doFilter(request, response);

        } catch (Exception ex) {

            log.error("event=request_exception method={} uri={} error={}",
                    method, uri, ex.getClass().getSimpleName());

            throw ex;

        } finally {

            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            boolean isSlow = duration > SLOW_REQUEST_THRESHOLD_MS;

            // Determine log level
            if (status >= 500) {
                log.error("event=request_end method={} uri={} status={} duration={}ms slow_request={}",
                        method, uri, status, duration, isSlow);
            } else if (status >= 400 || isSlow) {
                log.warn("event=request_end method={} uri={} status={} duration={}ms slow_request={}",
                        method, uri, status, duration, isSlow);
            } else {
                log.info("event=request_end method={} uri={} status={} duration={}ms slow_request={}",
                        method, uri, status, duration, isSlow);
            }

            MDC.clear();
        }
    }

    private boolean shouldSkipLogging(String uri) {
        return uri.startsWith("/error")
                || uri.startsWith("/favicon.ico")
                || uri.startsWith("/actuator");
    }
}