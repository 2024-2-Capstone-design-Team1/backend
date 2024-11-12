package com.project.server.repository;

import com.project.server.domain.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Medicine findByName(String name);

    // 상비약 이름의 중복 여부 확인 메서드 추가
    boolean existsByName(String name);
}
