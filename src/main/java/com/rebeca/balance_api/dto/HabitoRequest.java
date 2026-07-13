package com.rebeca.balance_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HabitoRequest(
        @NotBlank @Size(max = 100) String nombre
) {}