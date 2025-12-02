package com.heuron.patient_service.service;

import com.heuron.patient_service.dto.ImageUploadResponseDto;
import com.heuron.patient_service.entity.Patient;
import com.heuron.patient_service.event.FileCleanupEvent;
import com.heuron.patient_service.exception.ImageNotFoundException;
import com.heuron.patient_service.exception.PatientNotFoundException;
import com.heuron.patient_service.repository.PatientRepository;
import com.heuron.patient_service.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final PatientRepository patientRepository;
    private final FileUploadUtil fileUploadUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ImageUploadResponseDto uploadImage(Long patientId, MultipartFile file) throws IOException {
        log.info("Uploading image for patient: patientId={}, fileName={}, size={}",
            patientId, file.getOriginalFilename(), file.getSize());

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new PatientNotFoundException(patientId));

        // 기존 이미지 파일명 백업
        String oldImageFileName = patient.getImageFileName();

        // 새 이미지 저장
        String newFileName = fileUploadUtil.saveFile(file, patientId);

        // 환자 정보 업데이트
        patient.uploadImage(newFileName);
        patientRepository.save(patient);

        log.info("Image info saved to database: patientId={}, newFileName={}", patientId, newFileName);

        // 이벤트 발행 - 트랜잭션 커밋/롤백 시 파일 정리
        eventPublisher.publishEvent(
            FileCleanupEvent.forImageUpdate(oldImageFileName, newFileName)
        );

        return new ImageUploadResponseDto(
            patientId,
            newFileName,
            true
        );
    }

    @Transactional(readOnly = true)
    public Resource getImage(Long patientId) {
        log.debug("Fetching image for patient: patientId={}", patientId);

        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new PatientNotFoundException(patientId));

        if (!patient.hasImage()) {
            log.warn("No image found for patient: patientId={}", patientId);
            throw new ImageNotFoundException(patientId);
        }

        // FileUploadUtil을 통해 파일 로드 (하드코딩 제거)
        return fileUploadUtil.loadFileAsResource(patient.getImageFileName());
    }

    public String getImageMediaType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }
}
