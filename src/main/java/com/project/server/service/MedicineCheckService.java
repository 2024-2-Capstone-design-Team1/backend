package com.project.server.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicineCheckService {

    private final VisionService visionService;
    private final GPTService gptService;

    @Autowired
    public MedicineCheckService(VisionService visionService, GPTService gptService) {
        this.visionService = visionService;
        this.gptService = gptService;
    }

    // 이미지에서 텍스트를 추출하고, 추출한 텍스트로 상비약의 이름과 효능/효과를 GPT로부터 받는 메서드
    public String processImageWithGPT(byte[] imageBytes) {
        JSONObject responseJson = new JSONObject();

        try {
            // 1. 이미지에서 텍스트 추출
            String extractedText = visionService.extractTextFromImage(imageBytes);

            // 2. 지시사항 추가: 상비약의 이름과 효능/효과 요청
            String prompt = extractedText + "\n\n위의 내용을 기반으로 가장 유사한 상비약의 이름과 효능/효과를 간단히 알려줘. 예시 형식: 이름: [약 이름], 효능효과: [효능/효과]";
            String gptResponse = gptService.getGPTResponse(prompt);

            // 3. GPT 응답에서 약 이름과 효능/효과 추출
            String[] responseLines = gptResponse.split(",");
            String medicineName = extractValue(responseLines, "이름");
            String efficacy = extractValue(responseLines, "효능효과");

            if (medicineName.isEmpty() || efficacy.isEmpty()) {
                responseJson.put("status", "error");
                responseJson.put("error_message", "약 이름 또는 효능/효과를 추출할 수 없습니다.");
            } else {
                responseJson.put("status", "success");
                responseJson.put("medicine_name", medicineName);
                responseJson.put("efficacy", efficacy);
                responseJson.put("message", medicineName + " 약의 효능/효과가 성공적으로 반환되었습니다.");
            }
        } catch (Exception e) {
            // 예외 처리
            responseJson.put("status", "error");
            responseJson.put("error_message", "처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        return responseJson.toString();
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
