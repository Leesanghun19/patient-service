package com.heuron.patient_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.heuron.patient_service.exception.ErrorMessage.FILE_READ_ERROR;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileReadException extends RuntimeException {
    public FileReadException(String fileName) {
        super(FILE_READ_ERROR + fileName);
    }
}