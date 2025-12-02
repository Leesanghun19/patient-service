# 환자 서비스 (Patient Service)

## Introduction

병원에서 근무하는 의사가 환자의 병변 분석을 위해 환자 기본 정보와 이미지 파일을 업로드하여 저장하고 조회할 수 있는 REST API 기반 서비스입니다.

---

## 기술 스택

- **Java**: 17
- **Framework**: Spring Boot 3.5.8
- **ORM**: Spring Data JPA
- **Database**: MySQL 8
- **Build Tool**: Gradle
- **Dependencies**: Lombok

---

## 설치 및 실행 방법

### 요구사항

- Docker & Docker Compose 설치
- Java 17 설치 (로컬 개발 시)

### 초기 설정

#### 1. 테이블 생성

#### 애플리케이션 확인

```
http://localhost:8080/actuator/health
```

---

## 애플리케이션 접속

### 웹 UI (Thymeleaf)

브라우저에서 환자 정보를 관리할 수 있는 웹 인터페이스를 제공합니다.

- **메인 페이지**: `http://localhost:8080`
- **환자 목록 조회**: `http://localhost:8080/patients`
- **환자 등록 폼**: `http://localhost:8080/patients/new`

### API 문서 (Swagger UI)

Swagger UI를 통해 모든 REST API 엔드포인트를 확인하고 테스트할 수 있습니다.

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 데이터베이스 스키마

### Patient 테이블

```sql
CREATE TABLE IF NOT EXISTS patient (
  patient_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  age INT NOT NULL,
  gender VARCHAR(10) NOT NULL,
  has_disease BOOLEAN NOT NULL DEFAULT FALSE,
  image_file_name VARCHAR(255),
  is_image_uploaded BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 필드 설명

| 필드명 | 타입 | 설명 |
|--------|------|------|
| patient_id | BIGINT | 환자 고유 ID (PK) |
| name | VARCHAR(100) | 환자명 |
| age | INT | 환자 나이 |
| gender | VARCHAR(10) | 성별 (M/F) |
| has_disease | BOOLEAN | 질병 여부 |
| image_file_name | VARCHAR(255) | 저장된 이미지 파일명 |
| is_image_uploaded | BOOLEAN | 이미지 업로드 완료 여부 (조회 필터링 기준) |
| created_at | TIMESTAMP | 생성 시간 |
| updated_at | TIMESTAMP | 수정 시간 |

---

## 프로젝트 파일 구조

```
src/main/java/com/heuron/patient_service/
├── controller/
│   ├── PatientController.java           # REST API 엔드포인트
│   └── PatientViewController.java       # Thymeleaf 뷰 컨트롤러
├── service/
│   ├── PatientService.java              # 환자 비즈니스 로직
│   └── ImageService.java                # 이미지 처리 로직
├── repository/
│   └── PatientRepository.java           # JPA Repository
├── entity/
│   └── Patient.java                     # JPA Entity
├── dto/
│   ├── PatientRequestDto.java           # 환자 저장 요청
│   ├── PatientResponseDto.java          # 환자 응답
│   ├── ImageUploadResponseDto.java      # 이미지 업로드 응답
│   ├── PaginatedResponse.java           # 페이징 응답
│   └── ErrorResponse.java               # 에러 응답
├── exception/
│   ├── PatientNotFoundException.java    # 환자 미발견 예외
│   ├── ImageNotFoundException.java      # 이미지 미발견 예외
│   ├── InvalidImageException.java       # 파일 형식 오류 예외
│   ├── ImageUploadException.java        # 이미지 업로드 실패 예외
│   ├── FileReadException.java           # 파일 읽기 실패 예외
│   ├── ErrorCode.java                   # 에러 코드 상수
│   ├── ErrorMessage.java                # 에러 메시지 상수
│   └── GlobalExceptionHandler.java      # 전역 예외 처리기
├── util/
│   └── FileUploadUtil.java              # 파일 업로드 유틸리티
├── config/
│   ├── WebConfig.java                   # 정적 리소스 서빙 설정
│   └── SwaggerConfig.java               # Swagger/OpenAPI 설정
└── PatientServiceApplication.java       # 애플리케이션 시작점

src/main/resources/
├── application.properties                # 애플리케이션 설정
└── (uploads/ 디렉토리는 런타임에 생성)
```

