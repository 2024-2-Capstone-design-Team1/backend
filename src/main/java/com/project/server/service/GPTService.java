package com.project.server.service;

import com.project.server.dto.GPTRequest;
import com.project.server.dto.GPTResponse;
import com.project.server.domain.Medicine;
import com.project.server.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GPTService {

    private final RestTemplate restTemplate;
    private final MedicineRepository medicineRepository;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Value("${gpt.model}")
    private String model;

    public GPTService(RestTemplate restTemplate, MedicineRepository medicineRepository) {
        this.restTemplate = restTemplate;
        this.medicineRepository = medicineRepository;
    }

    public String getGPTResponse(String prompt) {
        // DB에서 모든 상비약의 이름을 가져오기
        List<String> allMedicineNames = medicineRepository.findAll().stream()
                .map(Medicine::getName)
                .collect(Collectors.toList());

        // 모든 상비약의 이름을 프롬프트에 추가
        String combinedPrompt = "현재 상비약 목록: " + String.join(", ", allMedicineNames) + "\n" + prompt + "이 증상일 때 먹어야 하는 상비약을 알려줘 약 목록: 형식으로 약 이름들만";

        // 결합된 프롬프트로 GPTRequest 생성
        GPTRequest request = new GPTRequest(model, combinedPrompt, 1, 256, 1, 0, 0);
        GPTResponse response = restTemplate.postForObject(apiUrl, request, GPTResponse.class);

        if (response != null && !response.getChoices().isEmpty()) {
            String gptContent = response.getChoices().get(0).getMessage().getContent();
            return filterRelevantMedicinesBySymptoms(gptContent, allMedicineNames);
        } else {
            return "GPT 응답이 없습니다.";
        }
    }

    // DB에 있는 이름들을 받아와서 현재 증상과 관련된 약의 이름만 필터링하여 반환하는 메서드
    private String filterRelevantMedicinesBySymptoms(String symptoms, List<String> allMedicineNames) {
        List<String> relevantMedicines = allMedicineNames.stream()
                .filter(name -> symptoms.toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());

        return relevantMedicines.isEmpty() ? "해당 증상에 맞는 약이 없습니다." : String.join(", ", relevantMedicines);
    }
}
