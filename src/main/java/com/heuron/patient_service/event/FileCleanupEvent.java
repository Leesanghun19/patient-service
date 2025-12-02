package com.heuron.patient_service.event;

/**
 * 파일 정리 이벤트
 * 트랜잭션 커밋/롤백 시 파일 시스템 정리를 위한 이벤트
 */
public record FileCleanupEvent(
    String oldFileName,
    String newFileName,
    CleanupType cleanupType
) {
    public enum CleanupType {
        DELETE_OLD_ON_COMMIT,      // 커밋 후 구 파일 삭제 (이미지 업데이트)
        DELETE_NEW_ON_ROLLBACK,    // 롤백 시 신규 파일 삭제
        DELETE_ON_COMMIT           // 커밋 후 파일 삭제 (환자 삭제)
    }

    /**
     * 이미지 업데이트 시 사용하는 이벤트 생성
     */
    public static FileCleanupEvent forImageUpdate(String oldFileName, String newFileName) {
        return new FileCleanupEvent(oldFileName, newFileName, CleanupType.DELETE_OLD_ON_COMMIT);
    }

    /**
     * 환자 삭제 시 사용하는 이벤트 생성
     */
    public static FileCleanupEvent forPatientDeletion(String fileName) {
        return new FileCleanupEvent(null, fileName, CleanupType.DELETE_ON_COMMIT);
    }
}