package com.project.server.service;

import com.project.server.domain.Medicine;
import com.project.server.dto.AddMedcineRequest;
import com.project.server.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MedicineRegistrationService {
    private final MedicineRepository medicineRepository;

    public Medicine save(AddMedcineRequest request){
        return medicineRepository.save(request.toEntity());
    }
}
