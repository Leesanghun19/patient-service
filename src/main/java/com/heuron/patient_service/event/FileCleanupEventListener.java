package com.heuron.patient_service.event;

import com.heuron.patient_service.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 파일 정리 이벤트 리스너
 * 트랜잭션 커밋/롤백 시 파일 시스템 정리를 담당
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileCleanupEventListener {

    private final FileUploadUtil fileUploadUtil;

    /**
     * 트랜잭션 커밋 후 파일 정리 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFileCleanupAfterCommit(FileCleanupEvent event) {
        log.debug("Transaction committed, processing file cleanup: {}", event);

        switch (event.cleanupType()) {
            case DELETE_OLD_ON_COMMIT -> {
                if (event.oldFileName() != null) {
                    fileUploadUtil.deleteFile(event.oldFileName());
                    log.info("Old file deleted after commit: {}", event.oldFileName());
                }
            }
            case DELETE_ON_COMMIT -> {
                if (event.newFileName() != null) {
                    fileUploadUtil.deleteFile(event.newFileName());
                    log.info("File deleted after commit: {}", event.newFileName());
                }
            }
            default -> log.debug("No file cleanup needed for commit phase");
        }
    }

    /**
     * 트랜잭션 롤백 후 파일 정리 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleFileCleanupAfterRollback(FileCleanupEvent event) {
        log.warn("Transaction rolled back, cleaning up new file: {}", event.newFileName());

        // 이미지 업데이트 실패 시 새로 저장한 파일 삭제
        if (event.newFileName() != null &&
            event.cleanupType() == FileCleanupEvent.CleanupType.DELETE_OLD_ON_COMMIT) {
            fileUploadUtil.deleteFile(event.newFileName());
            log.info("New file deleted after rollback: {}", event.newFileName());
        }
    }
}