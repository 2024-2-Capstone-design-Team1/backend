package com.project.server.service;

import com.project.server.dto.request.TranscriptionRequest;
import com.project.server.dto.request.WhisperTranscriptionRequest;
import com.project.server.dto.response.WhisperTranscriptionResponse;
import com.project.server.openaiclient.OpenAIClient;
import com.project.server.openaiclient.OpenAIClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RequiredArgsConstructor
@Service
public class OpenAIClientService {
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;

    public WhisperTranscriptionResponse createTranscription(TranscriptionRequest transcriptionRequest){

        MultipartFile file = transcriptionRequest.getFile();

        // OpenAI 요청 전 파일 정보 로그
        log.info("OpenAI 요청 준비 중 - 파일 이름: {}", file.getOriginalFilename());
        log.info("OpenAI 요청 준비 중 - 파일 크기: {} bytes", file.getSize());
        log.info("OpenAI 요청 준비 중 - 파일 MIME 타입: {}", file.getContentType());

        WhisperTranscriptionRequest whisperTranscriptionRequest = WhisperTranscriptionRequest.builder()
                .model("whisper-1")
                .file(transcriptionRequest.getFile())
                .build();

        log.info("OpenAI 요청 데이터 준비 완료 - 모델: {}, 파일 이름: {}", whisperTranscriptionRequest.getModel(), transcriptionRequest.getFile().getOriginalFilename());

        try {
            WhisperTranscriptionResponse response = openAIClient.createTranscription(whisperTranscriptionRequest);
            log.info("OpenAI 요청 성공 - 응답 데이터: {}", response);
            return response;
        } catch (Exception e) {
            log.error("OpenAI 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("OpenAI 요청 중 오류가 발생했습니다.", e);
        }}
}
