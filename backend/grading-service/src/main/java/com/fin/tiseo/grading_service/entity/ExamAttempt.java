package com.fin.tiseo.grading_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_attempts")
@PrimaryKeyJoinColumn(name = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
public class ExamAttempt extends StudentAssessment {

    @Column(nullable = false)
    private boolean graded;

    @Column(name = "exam_id", nullable = false)
    @NotNull
    @Positive
    private Long examId;

    @Column(name = "transaction_id", nullable = false, unique = true)
    @NotNull
    @Positive
    private Long transactionId;
}