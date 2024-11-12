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
        // GPTRequest 생성
        GPTRequest request = new GPTRequest(model, prompt, 1, 256, 1, 0, 0);
        GPTResponse response = restTemplate.postForObject(apiUrl, request, GPTResponse.class);

        if (response != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent().trim();
        } else {
            return "GPT 응답이 없습니다.";
        }
    }
}
