package com.fin.tiseo.grading_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_assessments")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class StudentAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull
    @PositiveOrZero
    private Integer points;

    @Column(name = "student_id", nullable = false)
    @NotNull
    @Positive
    private Long studentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    @NotNull
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grade_id", nullable = false)
    @NotNull
    private Grade grade;
}