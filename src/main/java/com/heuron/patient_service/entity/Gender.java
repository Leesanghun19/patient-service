package com.heuron.patient_service.entity;

public enum Gender {
    M("남성"),
    F("여성");

    private final String description;

    Gender(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}