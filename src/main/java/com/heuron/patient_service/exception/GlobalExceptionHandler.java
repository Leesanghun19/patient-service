package com.heuron.patient_service.exception;

import com.heuron.patient_service.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static com.heuron.patient_service.exception.ErrorMessage.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePatientNotFound(
            PatientNotFoundException e,
            HttpServletRequest request) {
        log.warn("Patient not found: {}", e.getMessage());
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({ImageNotFoundException.class, FileReadException.class})
    public ResponseEntity<ErrorResponse> handleImageNotFound(
            RuntimeException e,
            HttpServletRequest request) {
        log.warn("Image file issue: {}", e.getMessage());
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({InvalidImageException.class, ImageUploadException.class})
    public ResponseEntity<ErrorResponse> handleImageUploadException(
            RuntimeException e,
            HttpServletRequest request) {
        log.error("Image upload failed: {}", e.getMessage());
        ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedRuntimeException(
            RuntimeException e,
            HttpServletRequest request) {
        log.error("Unexpected RuntimeException: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 내부 오류가 발생했습니다",
            request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        return new ErrorResponse(
            LocalDateTime.now(),
            status.value(),
            status.name(),
            message,
            request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", errorMessage);

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_ERROR",
            errorMessage,
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(
            IOException e,
            HttpServletRequest request) {
        log.error("IOException occurred: {}", e.getMessage(), e);

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "IO_ERROR",
            "파일 처리 중 오류가 발생했습니다: " + e.getMessage(),
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception e,
            HttpServletRequest request) {
        log.error("Unexpected exception occurred: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);

        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            INTERNAL_SERVER_ERROR,
            request
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
