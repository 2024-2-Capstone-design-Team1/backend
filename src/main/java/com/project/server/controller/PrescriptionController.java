package com.project.server.controller;

import com.project.server.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    // 처방약 정보 확인
    @PostMapping("/process")
    public String processPrescription(@RequestParam("image") MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            return prescriptionService.processPrescription(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "이미지 처리 중 오류가 발생했습니다.";
        }
    }

    // 약 복용 업데이트
    @PostMapping("/update-dosage")
    public String updateDosage(@RequestParam("hospitalName") String hospitalName,
                               @RequestParam("timeOfDay") String timeOfDay) {
        return prescriptionService.updateDosage(hospitalName, timeOfDay);
    }
}
