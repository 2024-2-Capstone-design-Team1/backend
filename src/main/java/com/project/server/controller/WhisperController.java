package com.project.server.controller;

import com.project.server.dto.request.TranscriptionRequest;
import com.project.server.dto.response.WhisperTranscriptionResponse;
import com.project.server.service.OpenAIClientService;
import com.project.server.service.RecommendMedicineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
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
        MultipartFile file = transcriptionRequest.getFile();

        log.info("파일 요청 수신 - 파일 이름: {}", file.getOriginalFilename());
        log.info("파일 크기: {} bytes", file.getSize());
        log.info("파일 MIME 타입: {}", file.getContentType());

        if (!isValidAudioFormat(file.getOriginalFilename())) {
            log.error("지원되지 않는 파일 형식: {}", file.getOriginalFilename());
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "지원되지 않는 파일 형식입니다."
            ));
            }

        // Whisper를 사용하여 텍스트 추출
        WhisperTranscriptionResponse transcriptionResponse = openAIClientService.createTranscription(transcriptionRequest);
        try {
            log.info("OpenAI 요청 시작");
            transcriptionResponse = openAIClientService.createTranscription(transcriptionRequest);
        } catch (Exception e) {
            log.error("OpenAI 요청 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "OpenAI 요청 처리 중 오류가 발생했습니다."
            ));
        }


        // 추출된 텍스트를 RecommendMedicineService에 전달하여 약 추천 요청
        try {
            Map<String, Object> recommendedMedicines = recommendMedicineService.findMedicinesBySymptomOrName(transcriptionResponse.getText());
            log.info("약 추천 성공: {}", recommendedMedicines);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "transcriptionText", transcriptionResponse.getText(),
                    "recommendedMedicines", recommendedMedicines
            ));
        } catch (Exception e) {
            log.error("추천 약 정보를 가져오는 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "추천 약 정보를 가져오는 중 오류가 발생했습니다."
            ));
        }
    }

    private boolean isValidAudioFormat(String filename) {
        return filename != null && filename.matches(".*\\.(wav|mp3|ogg|flac|webm|m4a|mp4)$");
    }
}
