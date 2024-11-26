package com.project.server.controller;

import com.project.server.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/vision")
public class VisionController {

    private final VisionService visionService;

    @Autowired
    public VisionController(VisionService visionService) {
        this.visionService = visionService;
    }

    @PostMapping("/extract-text")
    public ResponseEntity<Map<String, Object>> extractText(@RequestParam("image") MultipartFile imageFile) {
        try {
            Map<String, Object> response = visionService.extractTextAsJson(imageFile);
            if ("error".equals(response.get("status"))) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "이미지 처리 중 오류가 발생했습니다."
            ));
        }
    }
}