package com.project.server.service;

import com.project.server.domain.Medicine;
import com.project.server.domain.Medicine_df;
import com.project.server.repository.MedicineDfRepository;
import com.project.server.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hibernate.internal.util.config.ConfigurationHelper.extractValue;

@RequiredArgsConstructor
@Service
public class MedicineService {

    private final VisionService visionService;
    private final GPTService gptService;
    private final MedicineRepository medicineRepository;

    private final MedicineDfRepository medicineDfRepository;

    //상비약 등록
    public Medicine save(Medicine medicine){
        return medicineRepository.save(medicine);
    }

    //상비약 조회
    public Medicine getMedicineByName(String name){
        return medicineRepository.findByName(name);
    }

    //삭제
    public boolean deleteMedicine(Long id){
        Medicine existingMedicine= medicineRepository.findById(id).orElse(null);
        if (existingMedicine != null){
            medicineRepository.delete(existingMedicine);
            return true;
        }
        return false;
    }

    //추출한 텍스트 저장
    public Medicine saveText(String name, String description) {
        Medicine medicine = Medicine.builder()
                .name(name)
                .description(description)
                .build();
        return medicineRepository.save(medicine);
    }

    // CSV 파일 저장
    public void saveCsvData(MultipartFile file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            br.readLine(); // 첫 번째 줄(헤더) 건너뜀
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", 2); // 첫 번째 열: name, 두 번째 열: description
                String name = data[0].trim();
                String description = data[1].trim();

                // Medicine_df 객체 생성 및 저장
                Medicine_df medicine = Medicine_df.builder()
                        .name(name)
                        .description(description)
                        .build();
                medicineDfRepository.save(medicine);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
        }
    }

    // 이미지로 상비약 삭제
    public Map<String, Object> deleteMedicineByImage(byte[] imageBytes) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. 이미지에서 텍스트 추출
            String extractedText = visionService.extractTextFromImage(imageBytes);

            if (extractedText == null || extractedText.isEmpty()) {
                response.put("status", "error");
                response.put("error_message", "이미지에서 텍스트를 식별할 수 없습니다. 이미지 품질이 낮거나 텍스트가 포함되어 있지 않을 수 있습니다. 다시 시도해주세요.");
                return response; // 에러 메시지 반환
            }

            // 2. GPT API 호출

            List<Medicine_df> allDfMedicines = medicineDfRepository.findAll();
            String dfData = allDfMedicines.stream()
                    .map(df -> "이름: " + df.getName() + ", 설명: " + df.getDescription())
                    .collect(Collectors.joining("; "));

            // 4. 상비약 로직 실행
            String prompt = "medicine_df 데이터:  "+ dfData + "를 참고하여 다음의 내용을 기반으로\n\n " + extractedText + " 가장 유사한 상비약의 이름과 효능/효과를 간단히 알려줘. 예시 형식: 이름: [약 이름]\n";
            String gptResponse = gptService.getGPTResponse(prompt);

            // 3. GPT 응답에서 약 이름 추출
            String medicineNameRaw = extractValue(gptResponse.split("\n"), "이름");
            String medicineName = medicineNameRaw.split("[\\(\\d\\s]")[0];

            // 4. 데이터베이스에서 약 조회 및 삭제
            Medicine medicine = medicineRepository.findByName(medicineName);

            if (medicine != null) {
                medicineRepository.delete(medicine);

                response.put("status", "success");
                response.put("name", medicineName);
                response.put("message", "삭제 완료");
                return response;
            }

            response.put("status", "not_found");
            response.put("name", medicineName);
            response.put("message", "해당 이름의 상비약을 찾을 수 없습니다.");
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error_message", "오류 발생: " + e.getMessage());
            return response;
        }
    }

    public Map<String, Object> deleteMedicineByText(String medicineName) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 3. 데이터베이스에서 약 조회 및 삭제
            Medicine medicine = medicineRepository.findByName(medicineName);
            if (medicine != null) {
                medicineRepository.delete(medicine);

                response.put("status", "success");
                response.put("name", medicineName);
                response.put("message", "삭제 완료");
                return response;
            }

            response.put("status", "not_found");
            response.put("name", medicineName);
            response.put("message", "해당 이름의 상비약을 찾을 수 없습니다.");
            return response;
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error_message", "오류 발생: " + e.getMessage());
            return response;
        }
    }


    private String extractValue(String[] parts, String key) {
        for (String part : parts) {
            if (part.trim().startsWith(key + ":")) {
                return part.split(":", 2)[1].trim();
            }
        }
        return "";
    }


}