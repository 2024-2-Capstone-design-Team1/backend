package com.project.server.repository;

import com.project.server.domain.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Medicine findByName(String name);
}
