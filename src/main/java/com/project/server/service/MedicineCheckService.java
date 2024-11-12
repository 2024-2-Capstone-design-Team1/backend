package com.project.server.service;

import com.project.server.domain.Medicine;
import com.project.server.repository.MedicineRepository;
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
        // 1. 이미지에서 텍스트 추출
        String extractedText = visionService.extractTextFromImage(imageBytes);

        // 2. 지시사항 추가: 상비약의 이름과 효능/효과 요청
        String prompt = extractedText + "\n\n위의 내용을 가지고 가장 유사한 상비약의 이름과 효능/효과를 간단히 알려줘. 예시 형식: [이름]입니다. 효능/효과로는 [효능/효과]";

        // 3. GPT API에 전달하고 응답 받기
        String gptResponse = gptService.getGPTResponse(prompt);

        // 4. 응답 반환
        return gptResponse;
    }
}
