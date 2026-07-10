package com.rebeca.balance_api.repository;

import com.rebeca.balance_api.model.Habito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitoRepository extends JpaRepository<Habito, Long> {

    List<Habito> findByUsuarioIdAndActivoTrueOrderByCreadoEnAsc(Long usuarioId);

    Optional<Habito> findByIdAndUsuarioId(Long id, Long usuarioId);

    long countByUsuarioIdAndActivoTrue(Long usuarioId);
}