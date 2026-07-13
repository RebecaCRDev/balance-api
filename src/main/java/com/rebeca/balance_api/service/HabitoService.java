package com.rebeca.balance_api.service;

import com.rebeca.balance_api.dto.HabitoResponse;
import com.rebeca.balance_api.repository.HabitoRepository;
import com.rebeca.balance_api.repository.RegistroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.rebeca.balance_api.dto.HabitoRequest;
import com.rebeca.balance_api.model.Habito;
import com.rebeca.balance_api.model.Usuario;
import com.rebeca.balance_api.repository.UsuarioRepository;

import com.rebeca.balance_api.model.Registro;

import com.rebeca.balance_api.dto.ProgresoResponse;

@Service
@RequiredArgsConstructor
public class HabitoService {

    private final HabitoRepository habitoRepository;
    private final RegistroRepository registroRepository;
    private final UsuarioRepository usuarioRepository;

    public List<HabitoResponse> listar(Long usuarioId) {
        Set<Long> completadosHoy = registroRepository
                .findByHabitoUsuarioIdAndFecha(usuarioId, LocalDate.now())
                .stream()
                .map(r -> r.getHabito().getId())
                .collect(Collectors.toSet());

        return habitoRepository.findByUsuarioIdAndActivoTrueOrderByCreadoEnAsc(usuarioId)
                .stream()
                .map(h -> new HabitoResponse(
                        h.getId(),
                        h.getNombre(),
                        h.getFrecuencia(),
                        completadosHoy.contains(h.getId())
                ))
                .toList();
    }

    public HabitoResponse crear(Long usuarioId, HabitoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));

        Habito habito = new Habito();
        habito.setUsuario(usuario);
        habito.setNombre(request.nombre().trim());

        Habito guardado = habitoRepository.save(habito);
        return new HabitoResponse(guardado.getId(), guardado.getNombre(), guardado.getFrecuencia(), false);
    }

    public HabitoResponse marcarCompletado(Long usuarioId, Long habitoId) {
        Habito habito = habitoRepository.findByIdAndUsuarioId(habitoId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado"));

        LocalDate hoy = LocalDate.now();

        if (registroRepository.findByHabitoIdAndFecha(habitoId, hoy).isEmpty()) {
            Registro registro = new Registro();
            registro.setHabito(habito);
            registro.setFecha(hoy);
            registroRepository.save(registro);
        }

        return new HabitoResponse(habito.getId(), habito.getNombre(), habito.getFrecuencia(), true);
    }

    public HabitoResponse desmarcarCompletado(Long usuarioId, Long habitoId) {
        Habito habito = habitoRepository.findByIdAndUsuarioId(habitoId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Hábito no encontrado"));

        registroRepository.findByHabitoIdAndFecha(habitoId, LocalDate.now())
                .ifPresent(registroRepository::delete);

        return new HabitoResponse(habito.getId(), habito.getNombre(), habito.getFrecuencia(), false);
    }

    public ProgresoResponse progresoHoy(Long usuarioId) {
        long total = habitoRepository.countByUsuarioIdAndActivoTrue(usuarioId);
        long completados = registroRepository.countByHabitoUsuarioIdAndFecha(usuarioId, LocalDate.now());

        int porcentaje = total == 0 ? 0 : (int) Math.round(completados * 100.0 / total);
        return new ProgresoResponse(completados, total, porcentaje);
    }
}