package com.project.server.service;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VisionService {

    // MultipartFile로 이미지를 받아서 텍스트를 추출하는 메서드
    public String extractTextFromImage(MultipartFile file) {
        try {
            byte[] imageBytes = file.getBytes();
            return extractTextFromImage(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 텍스트 추출 실패 시 null 반환
        }
    }

    // byte[]로 이미지를 받아서 텍스트를 추출하는 메서드
    public String extractTextFromImage(byte[] imageBytes) {
        try {
            ByteString imgBytes = ByteString.copyFrom(imageBytes);

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
                StringBuilder stringBuilder = new StringBuilder();
                for (AnnotateImageResponse res : batchResponse.getResponsesList()) {
                    if (res.hasError()) {
                        System.err.printf("Error: %s\n", res.getError().getMessage());
                        return null; // 오류 시 null 반환
                    }
                    stringBuilder.append(res.getFullTextAnnotation().getText());
                }
                return stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 처리 중 오류 발생 시 null 반환
        }
    }

    // JSON 형식으로 응답을 반환하는 메서드
    public Map<String, Object> extractTextAsJson(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            String extractedText = extractTextFromImage(file);
            if (extractedText == null || extractedText.isEmpty()) {
                response.put("status", "error");
                response.put("error_message", "텍스트 추출에 실패했습니다.");
            } else {
                response.put("status", "success");
                response.put("extracted_text", extractedText);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error_message", "처리 중 오류가 발생했습니다: " + e.getMessage());
        }
        return response;
    }
}