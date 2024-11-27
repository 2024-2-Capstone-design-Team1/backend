package com.project.server.service;

import com.project.server.domain.Medicine_df;
import com.project.server.repository.MedicineDfRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MedicineCheckService {

    private final VisionService visionService;
    private final GPTService gptService;

    private final MedicineDfRepository medicineDfRepository;


    public MedicineCheckService(VisionService visionService, GPTService gptService, MedicineDfRepository medicineDfRepository) {
        this.visionService = visionService;
        this.gptService = gptService;
        this.medicineDfRepository = medicineDfRepository;
    }

    // 이미지에서 텍스트를 추출하고, 추출한 텍스트로 상비약의 이름과 효능/효과를 GPT로부터 받는 메서드
    public Map<String, Object> processImageWithGPT(byte[] imageBytes) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. 이미지에서 텍스트 추출
            String extractedText = visionService.extractTextFromImage(imageBytes);

            if (extractedText == null || extractedText.isEmpty()) {
                response.put("status", "error");
                response.put("error_message", "이미지에서 텍스트를 식별할 수 없습니다. 이미지 품질이 낮거나 텍스트가 포함되어 있지 않을 수 있습니다. 다시 시도해주세요.");
                return response; // 에러 메시지 반환
            }

            List<Medicine_df> allDfMedicines = medicineDfRepository.findAll();
            String dfData = allDfMedicines.stream()
                    .map(df -> "이름: " + df.getName() + ", 설명: " + df.getDescription())
                    .collect(Collectors.joining("; "));

            // 4. 상비약 로직 실행
            String prompt = "medicine_df 데이터:  "+ dfData + "를 참고하여 다음의 내용을 기반으로\n\n " + extractedText + " 가장 유사한 상비약의 이름과 효능/효과를 간단히 알려줘. 예시 형식: 이름: [약 이름]\n효능효과: [효능/효과]";
            String gptResponse = gptService.getGPTResponse(prompt);

            String medicineNameRaw = extractValue(gptResponse.split("\n"), "이름");
            // () 또는 숫자 또는 띄어쓰기가 나오기 전까지만 추출
            String medicineName = medicineNameRaw.split("[\\(\\d\\s]")[0];

            String efficacy = extractValue(gptResponse.split("\n"), "효능효과");

//            // 3. GPT 응답에서 약 이름과 효능/효과 추출
//            String[] responseLines = gptResponse.split(",");
//            String medicineName = extractValue(responseLines, "이름");
//            String efficacy = extractValue(responseLines, "효능효과");

            if (medicineName.isEmpty() || efficacy.isEmpty()) {
                response.put("status", "error");
                response.put("error_message", "약 이름 또는 효능/효과를 추출할 수 없습니다.");
            } else {
                response.put("status", "success");
                response.put("medicine_name", medicineName);
                response.put("efficacy", efficacy);
                response.put("message", medicineName + " 약의 효능/효과가 성공적으로 반환되었습니다.");
            }
        } catch (Exception e) {
            // 예외 처리
            response.put("status", "error");
            response.put("error_message", "처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        return response;
    }

    // GPT 응답에서 특정 키 값 추출
    private String extractValue(String[] lines, String key) {
        for (String line : lines) {
            if (line.trim().startsWith(key + ":")) {
                return line.split(":", 2)[1].trim();
            }
        }
        return "";
    }
}