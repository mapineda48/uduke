package com.myapp.backend.model.dto;

import java.time.Instant;
import java.time.LocalDate;

public record PersonDto(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    LocalDate birthDate,
    String photoUrl,
    Instant createdAt
) {}
