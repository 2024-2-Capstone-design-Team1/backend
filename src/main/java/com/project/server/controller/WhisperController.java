package com.project.server.controller;

import com.project.server.dto.request.TranscriptionRequest;
import com.project.server.dto.response.WhisperTranscriptionResponse;
import com.project.server.service.OpenAIClientService;
import com.project.server.service.RecommendMedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class WhisperController {
    private final OpenAIClientService openAIClientService;
    private final RecommendMedicineService recommendMedicineService;

    @PostMapping(value = "/transcription-to-medicine", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> transcribeAndRecommendMedicine(
            @ModelAttribute TranscriptionRequest transcriptionRequest
    ) {

        // Whisper를 사용하여 텍스트 추출
        WhisperTranscriptionResponse transcriptionResponse = openAIClientService.createTranscription(transcriptionRequest);

        // 추출된 텍스트를 RecommendMedicineService에 전달하여 약 추천 요청
        try {
            Map<String, Object> recommendedMedicines = recommendMedicineService.findMedicinesBySymptomOrName(transcriptionResponse.getText());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "transcriptionText", transcriptionResponse.getText(),
                    "recommendedMedicines", recommendedMedicines
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "추천 약 정보를 가져오는 중 오류가 발생했습니다."
            ));
        }
    }
}
