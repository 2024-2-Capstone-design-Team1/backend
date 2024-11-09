package com.project.server.controller;


import com.project.server.service.CombinedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/combined")
public class CombinedController {

    private final CombinedService combinedService;

    @Autowired
    public CombinedController(CombinedService combinedService) {
        this.combinedService = combinedService;
    }

    @PostMapping("/process-image")
    public String processImage(@RequestParam("image") MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            return combinedService.processImageWithGPT(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing image.";
        }
    }
}
