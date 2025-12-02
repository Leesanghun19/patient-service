package com.heuron.patient_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이미지 업로드 응답")
public record ImageUploadResponseDto(
    @Schema(description = "환자 ID", example = "1")
    Long patientId,

    @Schema(description = "저장된 이미지 파일명", example = "1_1704879000000.jpg")
    String imageFileName,

    @Schema(description = "이미지 업로드 완료 여부", example = "true")
    Boolean isImageUploaded
) {}