package com.project.server.service;

import com.project.server.domain.Medicine;
import com.project.server.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendMedicineService {

    private final MedicineRepository medicineRepository;
    private final GPTService gptService;

    public RecommendMedicineService(MedicineRepository medicineRepository, GPTService gptService) {
        this.medicineRepository = medicineRepository;
        this.gptService = gptService;
    }

    public List<Medicine> findMedicinesBySymptomOrName(String symptomOrName) {
        // 현재 상비약 리스트를 불러와 이름과 설명만 추출하여 문자열로 정리
        List<Medicine> allMedicines = medicineRepository.findAll();
        String medicineList = allMedicines.stream()
                .map(medicine -> "이름: " + medicine.getName() + ", 설명: " + medicine.getDescription())
                .collect(Collectors.joining("; "));

        // GPT API 프롬프트 작성: 상비약 리스트와 증상 또는 약 이름을 포함
        String prompt = "현재 상비약 리스트에서 " + symptomOrName + "와 관련된 약을 추천해서 리스트의 이름과 똑같이 이름들만 반환. 상비약 리스트: " + medicineList;

        // GPT API에 프롬프트 전달
        String refinedInput = gptService.getGPTResponse(prompt);
        System.out.println(refinedInput);

        // GPT 응답(refinedInput)을 기준으로 데이터베이스에서 상비약을 검색
        List<Medicine> matchedMedicines = allMedicines.stream()
                .filter(medicine -> medicine.getName().contains(refinedInput) ||
                        (medicine.getDescription() != null && medicine.getDescription().contains(refinedInput)))
                .collect(Collectors.toList());

        // 관련 약이 없는 경우 예외 발생
        if (matchedMedicines.isEmpty()) {
            throw new IllegalArgumentException("존재하는 상비약이 없습니다.");
        }

        return matchedMedicines;
    }
}
