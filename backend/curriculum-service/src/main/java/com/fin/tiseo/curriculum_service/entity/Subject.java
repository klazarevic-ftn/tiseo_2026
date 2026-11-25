package com.fin.tiseo.curriculum_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subjects")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Subject {

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
    private Integer studyYear;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer ects;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "study_program_id", nullable = false)
    @NotNull
    private StudyProgram studyProgram;
}