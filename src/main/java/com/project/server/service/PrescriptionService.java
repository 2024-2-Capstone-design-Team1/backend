package com.project.server.service;

import com.project.server.domain.Prescription;
import com.project.server.repository.PrescriptionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PrescriptionService {

    private final VisionService visionService;
    private final GPTService gptService;
    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(VisionService visionService, GPTService gptService, PrescriptionRepository prescriptionRepository) {
        this.visionService = visionService;
        this.gptService = gptService;
        this.prescriptionRepository = prescriptionRepository;
    }

    // 이미지 처리 및 병원 이름 기반 처방약 정보 확인
    public Map<String, Object> processPrescription(byte[] imageBytes) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. 이미지에서 텍스트 추출
            String extractedText = visionService.extractTextFromImage(imageBytes);

            // 2. GPT를 사용해 병원 이름 추출
            String prompt = extractedText + "\n\n위 내용을 기반으로 병원 이름만 반환해줘. 예시 형식: 병원이름: [병원명]";
            String gptResponse = gptService.getGPTResponse(prompt);

            String hospitalName = extractValue(gptResponse, "병원이름");
            if (hospitalName.isEmpty()) {
                response.put("status", "error");
                response.put("error_message", "병원 이름을 인식할 수 없습니다. 다시 시도해주세요.");
                return response;
            }

            // 3. 병원 이름으로 처방약 검색
            Prescription prescription = prescriptionRepository.findByHospitalName(hospitalName);
            if (prescription == null) {
                response.put("status", "error");
                response.put("error_message", hospitalName + " 병원의 처방약이 데이터베이스에 없습니다.");
                return response;
            }

            // 4. 처방약 정보 JSON 반환
            int totalBags = parseIntSafe(prescription.getMorning()) +
                    parseIntSafe(prescription.getLunch()) +
                    parseIntSafe(prescription.getDinner());

            response.put("status", "success");
            response.put("hospital_name", prescription.getHospitalName());
            response.put("total_bags", totalBags);
            response.put("message", prescription.getHospitalName() +
                    " 처방약입니다. 아침, 점심, 저녁 식후 30분 " +
                    prescription.getTotalDays() + "일치로 총 " + totalBags + "봉투로 이루어져 있습니다.");
            return response;

        } catch (Exception e) {
            response.put("status", "error");
            response.put("error_message", "처리 중 오류가 발생했습니다: " + e.getMessage());
            return response;
        }
    }

    // 복용 상태 업데이트
    public Map<String, Object> updateDosage(String hospitalName, String timeOfDay) {
        Map<String, Object> response = new HashMap<>();

        try {
            Prescription prescription = prescriptionRepository.findByHospitalName(hospitalName);

            if (prescription == null) {
                response.put("status", "error");
                response.put("error_message", "해당 병원의 처방약이 없습니다.");
                return response;
            }

            // 해당 시간대 복용 개수 감소
            int remainingDoses = decreaseDosage(prescription, timeOfDay);

            // 남은 봉투 계산
            int morningRemaining = parseIntSafe(prescription.getMorning());
            int lunchRemaining = parseIntSafe(prescription.getLunch());
            int dinnerRemaining = parseIntSafe(prescription.getDinner());
            int totalRemainingBags = morningRemaining + lunchRemaining + dinnerRemaining;

            // 데이터베이스 저장
            prescriptionRepository.save(prescription);

            // 결과 JSON 반환
            response.put("status", "success");
            response.put("hospital_name", prescription.getHospitalName());
            response.put("time_of_day", timeOfDay);
            response.put("remaining_bags", totalRemainingBags);
            response.put("message", String.format(
                    "약 복용 완료. %s 복용 후 남은 봉투: 총 %d개 (아침: %d개, 점심: %d개, 저녁: %d개)",
                    timeOfDay, totalRemainingBags, morningRemaining, lunchRemaining, dinnerRemaining
            ));
            return response;

        } catch (Exception e) {
            response.put("status", "error");
            response.put("error_message", "처리 중 오류가 발생했습니다: " + e.getMessage());
            return response;
        }
    }

    // 특정 시간대 복용량 감소
    private int decreaseDosage(Prescription prescription, String timeOfDay) {
        int remainingDoses;

        switch (timeOfDay.toLowerCase()) {
            case "morning":
                remainingDoses = parseIntSafe(prescription.getMorning()) - 1;
                prescription.setMorning(String.valueOf(Math.max(remainingDoses, 0)));
                break;
            case "lunch":
                remainingDoses = parseIntSafe(prescription.getLunch()) - 1;
                prescription.setLunch(String.valueOf(Math.max(remainingDoses, 0)));
                break;
            case "dinner":
                remainingDoses = parseIntSafe(prescription.getDinner()) - 1;
                prescription.setDinner(String.valueOf(Math.max(remainingDoses, 0)));
                break;
            default:
                throw new IllegalArgumentException("잘못된 시간대입니다: " + timeOfDay);
        }

        return remainingDoses;
    }

    // 텍스트에서 특정 키 값 추출
    private String extractValue(String text, String key) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains(key)) {
                return line.split(":", 2)[1].trim();
            }
        }
        return ""; // 기본값 반환
    }

    // null-safe Integer 파싱
    private int parseIntSafe(String value) {
        if (value == null || value.isEmpty()) {
            return 0; // 기본값으로 0 반환
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // 파싱 실패 시 0 반환
        }
    }
}
