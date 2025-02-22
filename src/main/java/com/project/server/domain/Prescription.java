package com.project.server.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "Prescription")
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name = "hospital_name", nullable = false)
    private String hospitalName;

    @Column(name = "morning", nullable = true)
    private String morning;

    @Column(name = "lunch", nullable = true)
    private String lunch;

    @Column(name = "dinner", nullable = true)
    private String dinner;

    @Column(name = "timing", nullable = true)
    private String timing;

    @Column(name = "total_days", nullable = true)
    private String totalDays;

    @Column(name = "preparation_date", nullable = true)
    private String preparationDate; // 조제일 추가

    @Column(name = "description", nullable = true)
    private String description;

    @Builder
    public Prescription(String hospitalName, String morning, String lunch, String dinner, String timing, String totalDays, String preparationDate, String description) {
        this.hospitalName = hospitalName;
        this.morning = morning;
        this.lunch = lunch;
        this.dinner = dinner;
        this.timing = timing;
        this.totalDays = totalDays;
        this.preparationDate = preparationDate;
        this.description = description;
    }
}