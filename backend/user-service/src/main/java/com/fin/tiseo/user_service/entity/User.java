package com.fin.tiseo.user_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String uci;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String firstName;

    @Column(nullable = true)
    @Size(max = 255)
    private String middleName;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String lastName;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    @Email
    private String email;
}
