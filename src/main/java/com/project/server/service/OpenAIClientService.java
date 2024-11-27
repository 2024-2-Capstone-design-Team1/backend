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

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenAIClientService {
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;

    public WhisperTranscriptionResponse createTranscription(TranscriptionRequest transcriptionRequest) {
        MultipartFile file = transcriptionRequest.getFile();

        // 파일 임시 저장
        Path tempFile;
        try {
            tempFile = Files.createTempFile("uploaded_audio", ".mp3");
            file.transferTo(tempFile);
            log.info("임시 저장된 파일 경로: {}", tempFile.toAbsolutePath());
            log.info("파일 MIME 타입: {}", file.getContentType());
            log.info("파일 크기: {}", file.getSize());

            // 파일 MIME 타입 검증
            validateFileType(tempFile.toFile());
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }

        // WhisperTranscriptionRequest 생성
        WhisperTranscriptionRequest whisperTranscriptionRequest = WhisperTranscriptionRequest.builder()
                .model("whisper-1")
                .file(transcriptionRequest.getFile())
                .language("ko")
                .build();

        log.info("OpenAI 요청 데이터 준비 완료 - 모델: {}, 파일 이름: {}", whisperTranscriptionRequest.getModel(), transcriptionRequest.getFile().getOriginalFilename());

        try {
            WhisperTranscriptionResponse response = openAIClient.createTranscription(whisperTranscriptionRequest);
            log.info("OpenAI 요청 성공 - 응답 데이터: {}", response);
            return response;
        } catch (Exception e) {
            log.error("OpenAI 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("OpenAI 요청 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 파일 MIME 타입 검증
     *
     * @param file 저장된 파일
     */
    private void validateFileType(File file) {
        String mimeType;
        try {
            // 파일 내용 기반 MIME 타입 추론
            mimeType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("MIME 타입을 감지하는 중 오류 발생: " + e.getMessage(), e);
        }

        if (mimeType == null) {
            throw new IllegalArgumentException("파일 MIME 타입을 감지할 수 없습니다.");
        }

        log.info("감지된 MIME 타입: {}", mimeType);

        if (!mimeType.matches("audio/(mp3|mpeg|mp4|flac|wav|webm|ogg|oga|mpga|m4a)")) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다. 지원되는 형식: mp3, flac, wav 등.");
        }
    }
}
