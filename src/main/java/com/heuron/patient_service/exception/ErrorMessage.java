package com.heuron.patient_service.exception;

public final class ErrorMessage {
    private ErrorMessage() {
    }

    public static final String INTERNAL_SERVER_ERROR = "서버 오류가 발생했습니다.";

    // Patient-related errors
    public static final String PATIENT_NOT_FOUND = "환자를 찾을 수 없습니다. ID: ";

    // Image-related errors
    public static final String IMAGE_NOT_FOUND = "이미지를 찾을 수 없습니다. 환자 ID: ";
    public static final String IMAGE_UPLOAD_ERROR = "이미지 업로드 중 오류가 발생했습니다: ";

    // File validation errors
    public static final String FILE_NOT_SELECTED = "파일을 선택해주세요.";
    public static final String FILE_SIZE_EXCEEDED = "파일 크기는 10MB 이하여야 합니다.";
    public static final String INVALID_FILE_TYPE = "허용되지 않는 파일 형식입니다. (png, jpg만 가능)";

    // File operation errors
    public static final String FILE_READ_ERROR = "파일을 읽을 수 없습니다: ";
    public static final String FILE_DELETE_ERROR = "파일 삭제에 실패했습니다: ";
}
