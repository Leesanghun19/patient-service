package com.heuron.patient_service.dto;

import com.heuron.patient_service.entity.Gender;
import com.heuron.patient_service.entity.Patient;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "환자 정보 응답")
public record PatientResponseDto(
    @Schema(description = "환자 ID", example = "1")
    Long patientId,

    @Schema(description = "환자 이름", example = "김철수")
    String name,

    @Schema(description = "환자 나이", example = "45")
    Integer age,

    @Schema(description = "성별", example = "M", allowableValues = {"M", "F"})
    Gender gender,

    @Schema(description = "질병 여부", example = "true")
    Boolean hasDisease,

    @Schema(description = "이미지 URL", example = "/api/patients/1/image", nullable = true)
    String imageUrl,

    @Schema(description = "생성 일시", example = "2024-01-01T10:00:00")
    LocalDateTime createdAt,

    @Schema(description = "수정 일시", example = "2024-01-01T10:00:00")
    LocalDateTime updatedAt
) {
    public static PatientResponseDto from(Patient patient, String imageUrl) {
        return new PatientResponseDto(
            patient.getPatientId(),
            patient.getName(),
            patient.getAge(),
            patient.getGender(),
            patient.getHasDisease(),
            imageUrl,
            patient.getCreatedAt(),
            patient.getUpdatedAt()
        );
    }
}