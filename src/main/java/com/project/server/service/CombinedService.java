package com.project.server.service;

import com.project.server.domain.Medicine;
import com.project.server.domain.Prescription;
import com.project.server.repository.MedicineRepository;
import com.project.server.repository.PrescriptionRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CombinedService {

    private final VisionService visionService;
    private final GPTService gptService;
    private final MedicineRepository medicineRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public CombinedService(VisionService visionService, GPTService gptService, MedicineRepository medicineRepository, PrescriptionRepository prescriptionRepository) {
        this.visionService = visionService;
        this.gptService = gptService;
        this.medicineRepository = medicineRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public String processImageWithGPT(byte[] imageBytes) {
        JSONObject responseJson = new JSONObject(); // JSON 응답 생성

        try {
            // 1. 이미지에서 텍스트 추출
            String extractedText = visionService.extractTextFromImage(imageBytes);

            // 2. 처방약 로직 시도
            String prompt = extractedText + "\n\n위의 내용을 기반으로 다음 항목을 추출해줘:\n병원이름: 병원이름\n1일 총 투약횟수: 투약횟수 (숫자만)\n복용시간: (식전/식후)\n총일수: 일수 (숫자만)";
            String gptResponse = gptService.getGPTResponse(prompt);

            if (gptResponse.contains("병원이름:") && gptResponse.contains("총일수:")) {
                // 처방약 처리
                String[] responseParts = gptResponse.split("\n");
                String hospitalName = extractValue(responseParts, "병원이름");
                String dailyDoses = extractValue(responseParts, "1일 총 투약횟수").replaceAll("[^0-9]", "");
                String timing = extractValue(responseParts, "복용시간");
                String totalDays = extractValue(responseParts, "총일수").replaceAll("[^0-9]", "");

                if (prescriptionRepository.existsByHospitalName(hospitalName)) {
                    responseJson.put("status", "success");
                    responseJson.put("type", "prescription");
                    responseJson.put("name", hospitalName);
                    responseJson.put("message", "이미 등록됨");
                    return responseJson.toString();
                }

                // 약 봉지 수 계산 및 분배
                int dailyDosesCount = dailyDoses.isEmpty() ? 0 : Integer.parseInt(dailyDoses);
                int totalDaysCount = totalDays.isEmpty() ? 0 : Integer.parseInt(totalDays);
                int totalBags = dailyDosesCount * totalDaysCount;
                int perPeriodDoses = totalBags > 0 ? totalBags / 3 : 0;

                // 처방약 DB 저장
                Prescription prescription = Prescription.builder()
                        .hospitalName(hospitalName)
                        .morning(String.valueOf(perPeriodDoses))
                        .lunch(String.valueOf(perPeriodDoses))
                        .dinner(String.valueOf(perPeriodDoses))
                        .timing(timing)
                        .totalDays(totalDays)
                        .description("Generated by GPT based on extracted text.")
                        .build();
                prescriptionRepository.save(prescription);

                responseJson.put("status", "success");
                responseJson.put("type", "prescription");
                responseJson.put("name", hospitalName);
                responseJson.put("message", "등록됨.");
                return responseJson.toString();
            }

            // 3. 상비약 로직 실행
            prompt = extractedText + "\n\n위의 내용을 가지고 가장 유사한 상비약의 이름을 하나만 상비약 이름: 으로만 알려줘";
            gptResponse = gptService.getGPTResponse(prompt);

            String medicineName = gptResponse.replaceFirst("상비약 이름: ", "").split("\\(")[0].trim();

            if (medicineRepository.existsByName(medicineName)) {
                responseJson.put("status", "success");
                responseJson.put("type", "medicine");
                responseJson.put("name", medicineName);
                responseJson.put("message", "이미 등록됨");
                return responseJson.toString();
            }

            // 상비약 DB 저장
            Medicine medicine = Medicine.builder()
                    .name(medicineName)
                    .description("Generated by GPT based on extracted text.")
                    .build();
            medicineRepository.save(medicine);

            responseJson.put("status", "success");
            responseJson.put("type", "medicine");
            responseJson.put("name", medicineName);
            responseJson.put("message", "등록됨");
            return responseJson.toString();

        } catch (Exception e) {
            responseJson.put("status", "error");
            responseJson.put("error_message", e.getMessage());
            return responseJson.toString();
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
