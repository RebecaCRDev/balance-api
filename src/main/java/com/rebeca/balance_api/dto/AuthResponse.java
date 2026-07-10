package com.rebeca.balance_api.dto;

public record AuthResponse(String token, Long id, String nombre, String email) {}