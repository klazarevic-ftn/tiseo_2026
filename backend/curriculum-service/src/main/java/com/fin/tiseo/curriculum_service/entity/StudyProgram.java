package com.fin.tiseo.curriculum_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "study_programs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class StudyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String name;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer noOfSemesters;

    @Column(nullable = false)
    @NotNull
    @PositiveOrZero
    private Integer noOfStudents;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer totalECTS;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private StudyProgramType type;
}