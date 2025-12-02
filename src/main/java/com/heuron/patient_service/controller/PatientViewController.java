package com.heuron.patient_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PatientViewController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/patients/new")
    public String newPatientForm() {
        return "patient-form";
    }

    @GetMapping("/patients/{id}/upload")
    public String imageUploadForm(@PathVariable Long id, Model model) {
        model.addAttribute("patientId", id);
        return "image-upload";
    }
}
