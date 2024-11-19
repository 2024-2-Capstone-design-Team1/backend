package com.project.server.controller;

import com.project.server.service.MedicineCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/combined")
public class MedicineCheckController {

    private final MedicineCheckService medicineCheckService;

    @Autowired
    public MedicineCheckController(MedicineCheckService medicineCheckService) {
        this.medicineCheckService = medicineCheckService;
    }

    @PostMapping("/medicine-check")
    public ResponseEntity<Map<String, Object>> processImage(@RequestParam("image") MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            // Service에서 반환된 JSON 데이터를 그대로 반환
            Map<String, Object> response = medicineCheckService.processImageWithGPT(imageBytes);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 발생 시 JSON 형식으로 에러 응답 반환
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "이미지 처리 중 오류가 발생했습니다."
            ));
        }
    }
}
