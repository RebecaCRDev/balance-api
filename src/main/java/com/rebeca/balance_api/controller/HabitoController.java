package com.rebeca.balance_api.controller;

import com.rebeca.balance_api.dto.HabitoResponse;
import com.rebeca.balance_api.dto.ProgresoResponse;
import com.rebeca.balance_api.service.HabitoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.rebeca.balance_api.dto.HabitoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.Map;

import com.rebeca.balance_api.dto.ProgresoResponse;

@RestController
@RequestMapping("/api/habitos")
@RequiredArgsConstructor
public class HabitoController {

    private final HabitoService habitoService;

    @GetMapping
    public ResponseEntity<List<HabitoResponse>> listar(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(habitoService.listar(usuarioId));
    }

    @PostMapping
    public ResponseEntity<HabitoResponse> crear(
            Authentication authentication,
            @Valid @RequestBody HabitoRequest request
    ) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(habitoService.crear(usuarioId, request));
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<HabitoResponse> completar(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(habitoService.marcarCompletado(usuarioId, id));
    }

    @DeleteMapping("/{id}/completar")
    public ResponseEntity<HabitoResponse> descompletar(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(habitoService.desmarcarCompletado(usuarioId, id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @GetMapping("/progreso")
    public ResponseEntity<ProgresoResponse> progreso(Authentication authentication) {
        Long usuarioId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(habitoService.progresoHoy(usuarioId));
    }
}