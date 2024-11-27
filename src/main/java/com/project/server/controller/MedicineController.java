package com.project.server.controller;

import com.project.server.domain.Medicine;
import com.project.server.service.CombinedService;
import com.project.server.service.MedicineService;
import com.project.server.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/medicine")
public class MedicineController {
    private static final Logger logger = LoggerFactory.getLogger(MedicineController.class);


    @Autowired
    private MedicineService medicineService;

    @Autowired
    private VisionService visionService;

    /* TestController.java */
    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello  %s!",name);
    }

    @GetMapping("")
    public String helloWord(){return "heelo world";}

    //상비약 등록
    @PostMapping("/add")
    public ResponseEntity<Medicine> save(@RequestBody Medicine medicine){
        Medicine saveMedicine= medicineService.save(medicine);
        return new ResponseEntity<>(saveMedicine, HttpStatus.CREATED);
    }

    //약 이름으로 조회
    @GetMapping("/{name}")
    public ResponseEntity<Medicine> getMedicine(@PathVariable String name){
        Medicine medicine = medicineService.getMedicineByName(name);
        return new ResponseEntity<>(medicine, HttpStatus.OK);
    }

    //등록된 상비약 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicine(@PathVariable Long id){
        boolean deleted = medicineService.deleteMedicine(id);
        if (deleted){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); //삭제 완료
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  //해당 리소스 찾을 수 없음
        }
    }


    @PostMapping("/extract")
    public ResponseEntity<Medicine> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Google Vision API를 통해 이미지에서 텍스트 추출
            String extractedText = visionService.extractTextFromImage(file.getBytes());
            logger.info("Full Extracted Text: {}", extractedText); // 전체 추출 텍스트 로그
            // 텍스트의 처음 4글자는 name, 나머지는 description으로 분리
            String name = extractedText.length() >= 4 ? extractedText.substring(0, 4) : extractedText;
            String description = extractedText.length() > 4 ? extractedText.substring(4) : "";

            Medicine savedMedicine = medicineService.saveText(name, description);

            return new ResponseEntity<>(savedMedicine, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/delete-by-image")
    public ResponseEntity<?> deleteMedicineByImage(@RequestParam("image") MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            // MedicineService에서 JSON 응답(Map)을 반환받음
            Map<String, Object> response = medicineService.deleteMedicineByImage(imageBytes);
            // Map을 ResponseEntity로 감싸 반환
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 JSON 형식으로 에러 응답 반환
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "error_message", "이미지 처리 중 오류가 발생했습니다."
            ));
        }
    }







    // CSV 파일 업로드 및 저장
    @PostMapping("/upload-csv")
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        medicineService.saveCsvData(file);
        return ResponseEntity.ok("CSV data successfully saved to the database.");
    }


}