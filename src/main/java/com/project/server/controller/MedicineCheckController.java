package com.project.server.controller;

import com.project.server.service.MedicineCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/combined")
public class MedicineCheckController {

    private final MedicineCheckService medicineCheckService;

    @Autowired
    public MedicineCheckController(MedicineCheckService medicineCheckService) {
        this.medicineCheckService = medicineCheckService;
    }

    @PostMapping("/medicine-check")
    public String processImage(@RequestParam("image") MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            return medicineCheckService.processImageWithGPT(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing image.";
        }
    }
}
