package com.heuron.patient_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.heuron.patient_service.exception.ErrorMessage.IMAGE_NOT_FOUND;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(Long patientId) {
        super(IMAGE_NOT_FOUND + patientId);
    }
}