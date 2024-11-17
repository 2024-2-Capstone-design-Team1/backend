package com.project.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prescription")
@Getter
@Setter // Setter 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "hospital_name", nullable = false)
    private String hospitalName;

    @Column(name = "morning")
    private String morning;

    @Column(name = "lunch")
    private String lunch;

    @Column(name = "dinner")
    private String dinner;

    @Column(name = "timing")
    private String timing;

    @Column(name = "total_days")
    private String totalDays;

    @Column(name = "description")
    private String description;

    @Builder
    public Prescription(String hospitalName, String morning, String lunch, String dinner, String timing, String totalDays, String description) {
        this.hospitalName = hospitalName;
        this.morning = morning;
        this.lunch = lunch;
        this.dinner = dinner;
        this.timing = timing;
        this.totalDays = totalDays;
        this.description = description;
    }
}
