package com.heuron.patient_service.controller;

import com.heuron.patient_service.dto.ImageUploadResponseDto;
import com.heuron.patient_service.dto.PatientRequestDto;
import com.heuron.patient_service.dto.PatientResponseDto;
import com.heuron.patient_service.dto.PaginatedResponse;
import com.heuron.patient_service.service.ImageService;
import com.heuron.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Patient", description = "환자 정보 관리 API")
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final ImageService imageService;

    @Operation(summary = "환자 정보 저장", description = "환자의 기본 정보(이름, 나이, 성별, 질병 여부)를 저장합니다. (1단계)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "환자 정보 저장 성공",
            content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(@Valid @RequestBody PatientRequestDto requestDto) {
        PatientResponseDto response = patientService.createPatient(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "환자 목록 조회", description = "저장된 환자 정보를 페이징하여 조회합니다. imageUploaded=true인 환자만 필터링 가능합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "환자 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<PatientResponseDto>> getAllPatients(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "이미지 업로드 여부 필터 (true: 업로드된 환자만)") @RequestParam(required = false) Boolean imageUploaded) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<PatientResponseDto> response = patientService.getAllPatients(pageable, imageUploaded);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "환자 상세 조회", description = "특정 환자의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "환자 정보 조회 성공",
            content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음")
    })
    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponseDto> getPatientById(
            @Parameter(description = "환자 ID") @PathVariable Long patientId) {
        PatientResponseDto response = patientService.getPatientById(patientId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이미지 업로드", description = "환자의 이미지 파일(png, jpg)을 업로드합니다. 기존 이미지가 있을 경우 덮어씁니다. (2단계)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이미지 업로드 성공",
            content = @Content(schema = @Schema(implementation = ImageUploadResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 파일 형식"),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음")
    })
    @PutMapping("/{patientId}/image")
    public ResponseEntity<ImageUploadResponseDto> uploadImage(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "이미지 파일 (png, jpg)") @RequestParam("file") MultipartFile file) throws IOException {
        ImageUploadResponseDto response = imageService.uploadImage(patientId, file);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "이미지 조회", description = "환자의 이미지 파일을 조회합니다. 브라우저에서 직접 URL 입력 시 이미지가 표시됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이미지 조회 성공",
            content = @Content(mediaType = "image/jpeg")),
        @ApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    })
    @GetMapping("/{patientId}/image")
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "환자 ID") @PathVariable Long patientId) throws IOException {
        Resource resource = imageService.getImage(patientId);
        String mediaType = imageService.getImageMediaType(resource.getFilename());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
            .contentType(MediaType.parseMediaType(mediaType))
            .body(resource);
    }

    @Operation(summary = "환자 정보 삭제", description = "환자 정보와 관련 이미지 파일을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "환자 정보 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "환자를 찾을 수 없음")
    })
    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "환자 ID") @PathVariable Long patientId) {
        patientService.deletePatient(patientId);
        return ResponseEntity.noContent().build();
    }
}
