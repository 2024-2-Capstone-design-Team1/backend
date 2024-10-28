package hello.hello_spring.controller;

import hello.hello_spring.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class OCRController {

    @Autowired
    private VisionService visionService;

    @PostMapping("/extract-text")
    public String extractText(@RequestParam("file") MultipartFile file) {
        try {
            return visionService.extractTextFromImage(file);
        } catch (Exception e) {
            return "Failed to extract text: " + e.getMessage();
        }
    }
}
