package com.heuron.patient_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.heuron.patient_service.exception.ErrorMessage.PATIENT_NOT_FOUND;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long patientId) {
        super(PATIENT_NOT_FOUND + patientId);
    }
}
