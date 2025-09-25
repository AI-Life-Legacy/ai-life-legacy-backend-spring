package org.example.ailifelegacy.common.error;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 400 - 잘못된 요청 (Validation 실패 포함)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "message", errors
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "message", ex.getMessage()
        ));
    }

    // ✅ 401 - 인증 실패
    @ExceptionHandler(SecurityException.class) // 인증 관련 예외 (예: JWT 인증 실패)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
            "status", HttpStatus.UNAUTHORIZED.value(),
            "message", ex.getMessage() != null ? ex.getMessage() : "Unauthorized"
        ));
    }

    // ✅ 403 - 권한 거부
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
            "status", HttpStatus.FORBIDDEN.value(),
            "message", "접근 권한이 없습니다."
        ));
    }

    // ✅ 409 - 충돌
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
            "status", HttpStatus.CONFLICT.value(),
            "message", ex.getMessage()
        ));
    }

    // ✅ 500 - 서버 에러 (최종 fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalServerError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "message", ex.getMessage() != null ? ex.getMessage() : "Internal Server Error"
        ));
    }
}