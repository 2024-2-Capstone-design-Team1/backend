package hello.hello_spring.DTO;

import hello.hello_spring.domain.Medicine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddMedicineRequest {

    private String name;
    private String description;

    public Medicine toEntity(){
        return Medicine.builder()
                .name(name)
                .description(description)
                .build();


    }
}