package hello.hello_spring.repository;


import hello.hello_spring.domain.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Medicine findByName(String name);
}