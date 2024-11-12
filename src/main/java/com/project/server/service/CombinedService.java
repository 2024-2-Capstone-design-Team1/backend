package com.project.server.service;

import com.project.server.domain.Medicine;
import com.project.server.domain.Prescription;
import com.project.server.repository.MedicineRepository;
import com.project.server.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// 상비약 및 처방약 등록

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

    // 이미지에서 텍스트를 추출하고 ChatGPT API에 전달할 지시사항을 추가하여 GPT 응답을 받는 메서드
    public String processImageWithGPT(byte[] imageBytes) {
        // 1. 이미지에서 텍스트 추출
        String extractedText = visionService.extractTextFromImage(imageBytes);

        // 2. 처방약 로직을 먼저 시도
        String prompt = extractedText + "\n\n위의 내용을 기반으로 다음 항목을 추출해줘:\n병원이름: 병원이름,\n1일 총 투약횟수: 투약횟수 (숫자만),\n복용시간: (식전/식후),\n총일수: 일수 (숫자만)";
        String gptResponse = gptService.getGPTResponse(prompt);

        if (gptResponse.contains("병원이름:") && gptResponse.contains("총일수:")) {
            // 처방약 로직 실행
            String[] responseParts = gptResponse.split("\n");
            String hospitalName = extractValue(responseParts, "병원이름");
            String dailyDoses = extractValue(responseParts, "1일 총 투약횟수").replaceAll("[^0-9]", ""); // 숫자만 추출
            String timing = extractValue(responseParts, "복용시간");
            String totalDays = extractValue(responseParts, "총일수").replaceAll("[^0-9]", ""); // 숫자만 추출

            if (prescriptionRepository.existsByHospitalName(hospitalName)) {
                return "이미 등록된 약입니다.";
            }

            // 전체 약 봉지의 개수 계산 및 아침, 점심, 저녁 분배
            int dailyDosesCount = dailyDoses.isEmpty() ? 0 : Integer.parseInt(dailyDoses);
            int totalDaysCount = totalDays.isEmpty() ? 0 : Integer.parseInt(totalDays);
            int totalBags = dailyDosesCount * totalDaysCount;
            int perPeriodDoses = totalBags > 0 ? totalBags / 3 : 0;


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
            return hospitalName;
        }

        // 3. 처방약 로직이 실패한 경우에만 상비약 로직 실행
        prompt = extractedText + "\n\n위의 내용을 가지고 가장 유사한 상비약의 이름을 하나만 상비약 이름: 으로만 알려줘";
        gptResponse = gptService.getGPTResponse(prompt);

        // 응답에서 "상비약 이름: " 이후의 텍스트만 추출
        String medicineName = gptResponse.replaceFirst("상비약 이름: ", "").split(",")[0].trim();

        if (medicineRepository.existsByName(medicineName)) {
            return "이미 등록된 약입니다.";
        }

        if (!gptResponse.contains("해당 증상에 맞는 약이 없습니다")) {
            // 상비약 로직 실행
            Medicine medicine = Medicine.builder()
                    .name(medicineName)
                    .description("")
                    .build();

            medicineRepository.save(medicine);
            return medicineName + " 등록완료";
        }

        // 상비약 및 처방약 모두 실패한 경우
        return "Error: Unable to determine if the text relates to a prescription or general medicine.";
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
