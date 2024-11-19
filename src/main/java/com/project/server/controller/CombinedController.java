package com.project.server.controller;

import com.project.server.service.CombinedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/combined")
public class CombinedController {

    private final CombinedService combinedService;

    @Autowired
    public CombinedController(CombinedService combinedService) {
        this.combinedService = combinedService;
    }

    @PostMapping("/drug-registraion")
    public ResponseEntity<?> processImage(@RequestParam("image") MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            // CombinedService에서 JSON 응답을 생성하므로 이를 그대로 반환
            return combinedService.processImageWithGPT(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 JSON 형식으로 에러 응답 반환
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "이미지 처리 중 오류가 발생했습니다."
            ));
        }
    }
}
