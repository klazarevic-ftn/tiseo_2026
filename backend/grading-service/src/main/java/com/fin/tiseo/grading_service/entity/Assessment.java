package com.fin.tiseo.grading_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assessments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer maxPoints;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AssessmentType type;

    @Column(name = "subject_id", nullable = false)
    @NotNull
    @Positive
    private Long subjectId;
}