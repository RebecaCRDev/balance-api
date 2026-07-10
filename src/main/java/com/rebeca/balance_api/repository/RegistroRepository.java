package com.rebeca.balance_api.repository;

import com.rebeca.balance_api.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    Optional<Registro> findByHabitoIdAndFecha(Long habitoId, LocalDate fecha);

    List<Registro> findByHabitoUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

    long countByHabitoUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);
}