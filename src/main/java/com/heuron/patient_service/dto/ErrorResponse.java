package com.heuron.patient_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "에러 응답")
public record ErrorResponse(
    @Schema(description = "에러 발생 시각", example = "2024-01-10T10:30:00")
    LocalDateTime timestamp,

    @Schema(description = "HTTP 상태 코드", example = "404")
    int status,

    @Schema(description = "에러 코드", example = "NOT_FOUND")
    String error,

    @Schema(description = "에러 메시지", example = "환자를 찾을 수 없습니다. ID: 1")
    String message,

    @Schema(description = "요청 경로", example = "/api/patients/1")
    String path
) {}
