package com.heuron.patient_service.service;

import com.heuron.patient_service.dto.PatientRequestDto;
import com.heuron.patient_service.dto.PatientResponseDto;
import com.heuron.patient_service.dto.PaginatedResponse;
import com.heuron.patient_service.entity.Patient;
import com.heuron.patient_service.event.FileCleanupEvent;
import com.heuron.patient_service.exception.PatientNotFoundException;
import com.heuron.patient_service.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto requestDto) {
        log.info("Creating patient: name={}, age={}, gender={}", requestDto.name(), requestDto.age(), requestDto.gender());

        Patient patient = Patient.builder()
            .name(requestDto.name())
            .age(requestDto.age())
            .gender(requestDto.gender())
            .hasDisease(requestDto.hasDisease())
            .build();

        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient created successfully: patientId={}", savedPatient.getPatientId());

        return PatientResponseDto.from(savedPatient, null);
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<PatientResponseDto> getAllPatients(Pageable pageable, Boolean imageUploaded) {
        log.debug("Fetching patients: page={}, size={}, imageUploaded={}", pageable.getPageNumber(), pageable.getPageSize(), imageUploaded);

        Page<Patient> patients;
        if (imageUploaded == null) {
            // 필터 없음 - 모든 환자 조회
            patients = patientRepository.findAll(pageable);
        } else if (imageUploaded) {
            // 이미지가 있는 환자만 조회
            patients = patientRepository.findByImageFileNameIsNotNull(pageable);
        } else {
            // 이미지가 없는 환자만 조회
            patients = patientRepository.findByImageFileNameIsNullOrEmpty(pageable);
        }

        var content = patients.getContent().stream()
            .map(patient -> PatientResponseDto.from(patient,
                    patient.hasImage() ? getImageUrl(patient.getPatientId()) : null))
            .toList();

        log.debug("Fetched {} patients out of {} total", content.size(), patients.getTotalElements());

        return new PaginatedResponse<>(
            content,
            patients.getNumber(),
            patients.getSize(),
            patients.getTotalElements(),
            patients.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Long patientId) {
        return getPatientById(patientId, null);
    }

    @Transactional(readOnly = true)
    public PatientResponseDto getPatientById(Long patientId, Boolean imageUploaded) {
        log.debug("Fetching patient: patientId={}, imageUploaded={}", patientId, imageUploaded);

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new PatientNotFoundException(patientId));

        if (imageUploaded != null && imageUploaded && !patient.hasImage()) {
            log.warn("Patient found but has no image: patientId={}", patientId);
            throw new PatientNotFoundException(patientId);
        }

        String imageUrl = patient.hasImage() ? getImageUrl(patientId) : null;
        return PatientResponseDto.from(patient, imageUrl);
    }

    @Transactional
    public void deletePatient(Long patientId) {
        log.info("Deleting patient: patientId={}", patientId);

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new PatientNotFoundException(patientId));

        String imageFileName = patient.getImageFileName();

        patientRepository.delete(patient);
        log.info("Patient deleted from database: patientId={}", patientId);

        // 이벤트 발행 - 트랜잭션 커밋 시 파일 정리
        if (imageFileName != null) {
            eventPublisher.publishEvent(
                FileCleanupEvent.forPatientDeletion(imageFileName)
            );
        }
    }

    private String getImageUrl(Long patientId) {
        return "/api/patients/" + patientId + "/image";
    }
}
