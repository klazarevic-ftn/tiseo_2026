package com.fin.tiseo.grading_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "grades")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String subjectName;

    @Column(nullable = false)
    @NotNull
    @Min(5)
    @Max(10)
    private Integer value;

    @Column(name = "student_id", nullable = false)
    @NotNull
    @Positive
    private Long studentId;

    @Column(name = "subject_id", nullable = false)
    @NotNull
    @Positive
    private Long subjectId;
}