package hello.hello_spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CombinedService {

    private final VisionService visionService;
    private final GPTService gptService;

    @Autowired
    public CombinedService(VisionService visionService, GPTService gptService) {
        this.visionService = visionService;
        this.gptService = gptService;
    }

    // 이미지에서 텍스트를 추출하고 ChatGPT API에 전달할 지시사항을 추가하여 GPT 응답을 받는 메서드
    public String processImageWithGPT(byte[] imageBytes) {
        // 1. 이미지에서 텍스트 추출
        String extractedText = visionService.extractTextFromImage(imageBytes);

        // 2. 지시사항 추가
        String prompt = extractedText + "\n\n위의 내용을 가지고 가장 유사한 상비약의 이름을 하나만 상비약 이름: 으로만 알려줘";

        // 3. GPT API에 전달하고 응답 반환
        return gptService.getGPTResponse(prompt);
    }
}
