package com.project.server.service;

import com.project.server.dto.GPTRequest;
import com.project.server.dto.GPTResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GPTService {

    private final RestTemplate restTemplate;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Value("${gpt.model}")
    private String model;

    public GPTService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getGPTResponse(String prompt) {
        // 기본 메시지와 사용자 입력을 결합
        String combinedPrompt = prompt + "이 증상일 때 먹어야 하는 상비약을 알려줘 약 목록: 형식으로 약 이름들만";

        // 결합된 프롬프트로 GPTRequest 생성
        GPTRequest request = new GPTRequest(model, combinedPrompt, 1, 256, 1, 0, 0);
        GPTResponse response = restTemplate.postForObject(apiUrl, request, GPTResponse.class);

        if (response != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        } else {
            return "GPT 응답이 없습니다.";
        }
    }

}
