-- 데이터베이스 생성 및 선택
CREATE DATABASE IF NOT EXISTS patient_service;
  USE patient_service;

  -- Patient 테이블 생성
CREATE TABLE IF NOT EXISTS patient (
    patient_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    has_disease BOOLEAN NOT NULL DEFAULT FALSE,
    image_file_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
