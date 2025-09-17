package org.example.ailifelegacy.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Spring Security, JPA, 일반 예외까지 모두 잡음
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = "Internal Server Error";

        if (e instanceof ResponseStatusException rse) {
            // 스프링이 던지는 ResponseStatusException 처리
            status = rse.getStatusCode().value();
            message = rse.getReason() != null ? rse.getReason() : rse.getMessage();
        } else if (e instanceof IllegalArgumentException iae) {
            // 우리가 throw new IllegalArgumentException(...) 했을 경우
            status = HttpStatus.BAD_REQUEST.value();
            message = iae.getMessage();
        } else if (e instanceof IllegalStateException ise) {
            status = HttpStatus.CONFLICT.value();
            message = ise.getMessage();
        } else {
            // 기타 예외
            message = e.getMessage() != null ? e.getMessage() : message;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}
