package com.project.server.controller;

import com.project.server.domain.Medicine;
import com.project.server.service.RecommendMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Object> findMedicinesBySymptomOrName(@RequestParam("symptomOrName") String symptomOrName) {
        try {
            List<Medicine> medicines = recommendMedicineService.findMedicinesBySymptomOrName(symptomOrName);
            return new ResponseEntity<>(medicines, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("존재하는 상비약이 없습니다.", HttpStatus.NOT_FOUND);
        }
    }
}
