package com.heuron.patient_service.repository;

import com.heuron.patient_service.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // imageUploaded=true: 이미지가 있는 환자만 조회
    Page<Patient> findByImageFileNameIsNotNull(Pageable pageable);

    // imageUploaded=false: 이미지가 없는 환자만 조회
    @Query("SELECT p FROM Patient p WHERE p.imageFileName IS NULL OR p.imageFileName = ''")
    Page<Patient> findByImageFileNameIsNullOrEmpty(Pageable pageable);
}