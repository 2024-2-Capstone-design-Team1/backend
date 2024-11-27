package com.project.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "Medicine_df")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medicine_df {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name ="name",nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Builder
    public Medicine_df(String name, String description){
        this.name=name;
        this.description=description;
    }


}