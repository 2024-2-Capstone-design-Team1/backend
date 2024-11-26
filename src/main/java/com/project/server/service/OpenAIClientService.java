package com.project.server.service;

import com.project.server.dto.request.TranscriptionRequest;
import com.project.server.dto.request.WhisperTranscriptionRequest;
import com.project.server.dto.response.WhisperTranscriptionResponse;
import com.project.server.openaiclient.OpenAIClient;
import com.project.server.openaiclient.OpenAIClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OpenAIClientService {
    private final OpenAIClient openAIClient;
    private final OpenAIClientConfig openAIClientConfig;

    public WhisperTranscriptionResponse createTranscription(TranscriptionRequest transcriptionRequest){
        WhisperTranscriptionRequest whisperTranscriptionRequest = WhisperTranscriptionRequest.builder()
                .model("whisper-1")
                .file(transcriptionRequest.getFile())
                .build();
        return openAIClient.createTranscription(whisperTranscriptionRequest);
    }
}
