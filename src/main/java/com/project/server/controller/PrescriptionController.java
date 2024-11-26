package com.project.server.controller;

import com.project.server.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> processPrescription(@RequestParam("image") MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            Map<String, Object> response = prescriptionService.processPrescription(imageBytes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "이미지 처리 중 오류가 발생했습니다."
            ));
        }
    }

    // 약 복용 업데이트
    @PostMapping("/update-dosage")
    public ResponseEntity<Map<String, Object>> updateDosage(@RequestParam("hospitalName") String hospitalName,
                                                            @RequestParam("timeOfDay") String timeOfDay) {
        try {
            Map<String, Object> response = prescriptionService.updateDosage(hospitalName, timeOfDay);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "복용 업데이트 중 오류가 발생했습니다."
            ));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getPrescriptionCount(@RequestParam("hospitalName") String hospitalName) {
        try {
            Map<String, Object> response = prescriptionService.getPrescriptionCount(hospitalName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "처방전 조회 중 오류가 발생했습니다."
            ));
        }
    }
}