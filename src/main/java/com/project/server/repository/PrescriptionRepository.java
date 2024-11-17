package com.project.server.repository;

import com.project.server.domain.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // 병원 이름의 중복 여부 확인 메서드 추가
    boolean existsByHospitalName(String hospitalName);

    Prescription findByHospitalName(String hospitalName);

}
