package com.heuron.patient_service.dto;

import com.heuron.patient_service.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "환자 정보 저장 요청")
public record PatientRequestDto(
    @Schema(description = "환자 이름", example = "김철수", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 1, max = 100, message = "이름은 1-100자 사이여야 합니다")
    String name,

    @Schema(description = "환자 나이", example = "45", minimum = "0", maximum = "150", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "나이는 필수입니다")
    @Min(value = 0, message = "나이는 0 이상이어야 합니다")
    @Max(value = 150, message = "나이는 150 이하여야 합니다")
    Integer age,

    @Schema(description = "성별", example = "M", allowableValues = {"M", "F"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "성별은 필수입니다")
    Gender gender,

    @Schema(description = "질병 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "질병 여부는 필수입니다")
    Boolean hasDisease
) {}