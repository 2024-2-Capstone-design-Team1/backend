package com.project.server.service;

import com.project.server.domain.Medicine;
import com.project.server.repository.MedicineRepository;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendMedicineService {

    private final MedicineRepository medicineRepository;
    private final GPTService gptService;

    public RecommendMedicineService(MedicineRepository medicineRepository, GPTService gptService) {
        this.medicineRepository = medicineRepository;
        this.gptService = gptService;
    }

    public Map<String, Object> findMedicinesBySymptomOrName(String symptomOrName) {
        Map<String, Object> response = new HashMap<>(); // JSON 응답 객체 생성

        try {
            // 현재 상비약 리스트를 불러와 이름과 설명만 추출하여 문자열로 정리
            List<Medicine> allMedicines = medicineRepository.findAll();
            String medicineList = allMedicines.stream()
                    .map(medicine -> "이름: " + medicine.getName() + ", 설명: " + medicine.getDescription())
                    .collect(Collectors.joining("; "));

            // GPT API 프롬프트 작성: 상비약 리스트와 증상 또는 약 이름을 포함
            String prompt = "현재 상비약 리스트에서 " + symptomOrName + "와 관련된 약을 추천해서 리스트의 이름과 똑같이 이름들만 반환. 상비약 리스트: " + medicineList;

            // GPT API에 프롬프트 전달
            String refinedInput = gptService.getGPTResponse(prompt);

            // GPT 응답(refinedInput)을 기준으로 데이터베이스에서 상비약을 검색
            List<Medicine> matchedMedicines = allMedicines.stream()
                    .filter(medicine -> medicine.getName().contains(refinedInput) ||
                            (medicine.getDescription() != null && medicine.getDescription().contains(refinedInput)))
                    .collect(Collectors.toList());

            // 약 이름 리스트로 변환
            List<String> medicineNames = matchedMedicines.stream()
                    .map(Medicine::getName)
                    .collect(Collectors.toList());

            // 관련 약이 없는 경우 처리
            if (medicineNames.isEmpty()) {
                response.put("status", "error");
                response.put("error_message", "존재하는 상비약이 없습니다.");
            } else {
                // 약 이름을 JSON 배열로 추가
                response.put("status", "success");
                response.put("medicines", medicineNames); // 약 이름 리스트
            }
        } catch (Exception e) {
            // 예외 처리
            response.put("status", "error");
            response.put("error_message", "처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        return response; // JSON 응답 객체 반환
    }
}