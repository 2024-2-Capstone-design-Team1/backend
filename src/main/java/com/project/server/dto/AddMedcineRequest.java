package com.project.server.dto;

import com.project.server.domain.Medicine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddMedcineRequest {

    private String name;
    private String description;

    public Medicine toEntity(){
        return Medicine.builder()
                .name(name)
                .description(description)
                .build();


    }
}
