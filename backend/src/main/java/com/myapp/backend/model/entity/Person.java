package com.myapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "people")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 200, unique = true)
    private String email;

    @Column(length = 20)
    private String phone;

    private LocalDate birthDate;

    @Column(length = 500)
    private String photoUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}
