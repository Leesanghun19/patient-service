package com.heuron.patient_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "페이징 응답")
public record PaginatedResponse<T>(
    @Schema(description = "현재 페이지 데이터 목록")
    List<T> content,

    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    int pageNumber,

    @Schema(description = "페이지 크기", example = "10")
    int pageSize,

    @Schema(description = "전체 항목 수", example = "25")
    long totalElements,

    @Schema(description = "전체 페이지 수", example = "3")
    int totalPages
) {}
