package com.project.server.controller;

import com.project.server.service.RecommendMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
public class RecommendMedicineController {

    private final RecommendMedicineService recommendMedicineService;

    @Autowired
    public RecommendMedicineController(RecommendMedicineService recommendMedicineService) {
        this.recommendMedicineService = recommendMedicineService;
    }

    // Find related medicines by symptom or name
    @GetMapping("/find-medicine")
    public ResponseEntity<String> findMedicinesBySymptomOrName(@RequestParam("symptomOrName") String symptomOrName) {
        try {
            // Service 메서드 호출 (JSON 형식 반환)
            String jsonResponse = recommendMedicineService.findMedicinesBySymptomOrName(symptomOrName);
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            // 에러 처리
            String errorResponse = "{\"status\": \"error\", \"error_message\": \"" + e.getMessage() + "\"}";
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
