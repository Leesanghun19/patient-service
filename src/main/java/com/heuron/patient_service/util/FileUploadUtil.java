package com.heuron.patient_service.util;

import com.heuron.patient_service.exception.FileReadException;
import com.heuron.patient_service.exception.InvalidImageException;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import static com.heuron.patient_service.exception.ErrorMessage.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Set;

/**
 * 파일 업로드 유틸리티
 * 파일 검증, 저장, 삭제 기능 제공
 */
@Component
public class FileUploadUtil {

    @Value("${file.upload-dir:uploads/images}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "png"};
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
        "image/jpeg",
        "image/png"
    );

    private final Tika tika = new Tika();  // MIME 타입 검출기

    /**
     * 파일 저장
     */
    public String saveFile(MultipartFile file, Long patientId) throws IOException {
        validateFile(file);

        String fileName = generateFileName(file.getOriginalFilename(), patientId);
        Path uploadPath = Paths.get(uploadDir);

        // 디렉토리 생성
        Files.createDirectories(uploadPath);

        // 파일 저장
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, file.getBytes());

        return fileName;
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir, fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(FILE_DELETE_ERROR + fileName, e);
        }
    }

    /**
     * 파일을 Resource로 로드
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileReadException(fileName);
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new FileReadException(fileName);
        }
    }

    /**
     * 파일 검증 (보안 강화)
     * 1. 파일 존재 여부
     * 2. 파일 크기
     * 3. 파일명 정제 (Path Traversal 방어)
     * 4. 확장자 검증
     * 5. MIME 타입 검증 (실제 파일 내용 확인)
     */
    private void validateFile(MultipartFile file) throws IOException {
        // 1. 파일 존재 여부
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException(FILE_NOT_SELECTED);
        }

        // 2. 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidImageException(FILE_SIZE_EXCEEDED);
        }

        // 3. 파일명 정제 및 검증 (Path Traversal 방어)
        String originalFilename = sanitizeFileName(file.getOriginalFilename());
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new InvalidImageException("유효하지 않은 파일명입니다");
        }
        if (originalFilename.contains("..")) {
            throw new InvalidImageException("파일명에 '..'이 포함될 수 없습니다");
        }

        // 4. 확장자 검증
        String extension = getFileExtension(originalFilename);
        if (!isAllowedExtension(extension)) {
            throw new InvalidImageException(INVALID_FILE_TYPE);
        }

        // 5. MIME 타입 검증 (실제 파일 내용 확인)
        String mimeType = tika.detect(file.getBytes());
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new InvalidImageException(
                "파일 형식이 올바르지 않습니다. 허용된 형식: JPG, PNG (실제 타입: " + mimeType + ")"
            );
        }
    }

    /**
     * 파일명 정제 (Path Traversal 방어)
     * - 경로 구분자 제거 (/, \)
     * - ".." 제거
     * - 특수문자 제거
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return null;
        }

        // 경로 구분자 제거
        String sanitized = fileName.replaceAll("[/\\\\]", "");

        // ".." 제거
        sanitized = sanitized.replaceAll("\\.\\.", "");

        // 특수문자 제거 (파일명과 확장자만 허용: 알파벳, 숫자, 점, 하이픈, 언더스코어)
        sanitized = sanitized.replaceAll("[^a-zA-Z0-9._-]", "");

        return sanitized;
    }

    /**
     * 파일명 생성 (타임스탬프 포함)
     */
    private String generateFileName(String originalFileName, Long patientId) {
        String extension = getFileExtension(sanitizeFileName(originalFileName));
        long timestamp = Instant.now().toEpochMilli();
        return String.format("%d_%d.%s", patientId, timestamp, extension);
    }

    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 허용된 확장자 검증
     */
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}