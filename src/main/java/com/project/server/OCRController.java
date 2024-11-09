package com.project.server;

import com.project.server.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class OCRController {

    @Autowired
    private VisionService visionService;

    @GetMapping("/extract-text")
    public String extractText(@RequestParam("file") MultipartFile file) {
        try {
            return visionService.extractTextFromImage(file.getBytes());
        } catch (Exception e) {
            //e.printStackTrace();
            return "Failed to extract text: " + e.getMessage();
        }
    }
}
