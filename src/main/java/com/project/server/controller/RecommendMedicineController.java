package com.project.server.controller;

import com.project.server.service.RecommendMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> findMedicinesBySymptomOrName(@RequestParam("symptomOrName") String symptomOrName) {
        try {
            // Service 메서드 호출 (Map 형식 반환)
            Map<String, Object> response = recommendMedicineService.findMedicinesBySymptomOrName(symptomOrName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 발생 시 JSON 형식으로 에러 응답 반환
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "요청 처리 중 오류가 발생했습니다."
            ));
        }
    }
}
