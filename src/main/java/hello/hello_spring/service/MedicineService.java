package hello.hello_spring.service;


import hello.hello_spring.domain.Medicine;
import hello.hello_spring.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;

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
}