package com.project.server.service;

import com.project.server.domain.Medicine;
import com.project.server.domain.Medicine_df;
import com.project.server.repository.MedicineDfRepository;
import com.project.server.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendMedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineDfRepository medicineDfRepository;
    private final GPTService gptService;

    public RecommendMedicineService(MedicineRepository medicineRepository, MedicineDfRepository medicineDfRepository, GPTService gptService) {
        this.medicineRepository = medicineRepository;
        this.medicineDfRepository = medicineDfRepository;
        this.gptService = gptService;
    }

    public Map<String, Object> findMedicinesBySymptomOrName(String symptom) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. medicine_df 데이터 가져오기
            List<Medicine_df> allDfMedicines = medicineDfRepository.findAll();
            String dfData = allDfMedicines.stream()
                    .map(df -> "이름: " + df.getName() + ", 설명: " + df.getDescription())
                    .collect(Collectors.joining("; "));

            // 2. medicine 데이터 가져오기
            List<Medicine> allMedicines = medicineRepository.findAll();
            String medicineNames = allMedicines.stream()
                    .map(Medicine::getName)
                    .collect(Collectors.joining(", "));

            // 디버깅 로그
            System.out.println("Medicine Names (from DB): " + medicineNames);

            // 3. GPT 프롬프트 생성
            String prompt = "다음은 증상과 관련된 약을 추천하는 작업입니다: 최종 반환값은 반드시 약 이름만 쉼표(,)로 구분된 문자열로 반환해야합니다.\n" +
                    "1. 아래 제공된 medicine_df 데이터에서 증상과 관련된 약 이름을 추출합니다.\n" +
                    "2. 추출된 약 이름을 기준으로, 다음의 전체 medicine 이름에서 동일한 약 이름만 찾습니다.\n" +
                    "3. 최종 결과로, 동일한 약 이름만 쉼표(,)로 구분된 문자열로만 반환하세요.\n" +
                    "반환 형식:\n" +
                    "- 약 이름만 쉼표(,)로 구분된 문자열 (예: 타이레놀, 판콜에스내복액)\n" +
                    "- 괄호, 설명 또는 추가 데이터는 포함하지 마세요.\n" +
                    "증상: " + symptom + "\n" +
                    "medicine_df 데이터: " + dfData + "\n" +
                    "전체 medicine 이름: " + medicineNames;


            // 4. GPT API 호출
            String gptResponse = gptService.getGPTResponse(prompt);

            // 디버깅 로그
            System.out.println("GPT Response: " + gptResponse);

            // 5. GPT 응답에서 약 이름 추출
            List<String> extractedNames = Arrays.stream(gptResponse.split(","))
                    .map(String::trim)
                    .filter(name -> !name.isEmpty()) // 빈 값 제거
                    .collect(Collectors.toList());

            if (extractedNames.isEmpty()) {
                response.put("status", "not_found");
                response.put("message", "GPT 응답에서 약 이름을 추출할 수 없습니다.");
                return response;
            }

            // 6. 약 이름 매칭
            List<Medicine> matchedMedicines = medicineRepository.findByNameIn(extractedNames);

            if (matchedMedicines.isEmpty()) {
                response.put("status", "not_found");
                response.put("message", "해당 약을 상비약 목록에서 찾을 수 없습니다.");
                return response;
            }

            // 7. 매칭된 약 이름 리스트 생성
            List<String> medicineNamesList = matchedMedicines.stream()
                    .map(Medicine::getName)
                    .collect(Collectors.toList());

            response.put("status", "success");
            response.put("medicines", medicineNamesList);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "처리 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace(); // 디버깅을 위한 예외 출력
        }

        return response;
    }
}