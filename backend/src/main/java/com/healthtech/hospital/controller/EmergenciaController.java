package com.healthtech.hospital.controller;

import com.healthtech.hospital.model.AtendimentoEmergencia;
import com.healthtech.hospital.model.Paciente;
import com.healthtech.hospital.model.Profissional;
import com.healthtech.hospital.model.Medico;
import com.healthtech.hospital.model.enums.StatusEmergencia;
import com.healthtech.hospital.repository.AtendimentoEmergenciaRepository;
import com.healthtech.hospital.repository.PacienteRepository;
import com.healthtech.hospital.repository.ProfissionalRepository;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/emergencias")
public class EmergenciaController {
    private final AtendimentoEmergenciaRepository emergenciaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;

    public EmergenciaController(AtendimentoEmergenciaRepository emergenciaRepository, PacienteRepository pacienteRepository, ProfissionalRepository profissionalRepository) {
        this.emergenciaRepository = emergenciaRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    @GetMapping
    public List<AtendimentoEmergencia> listar(@RequestParam(required = false) StatusEmergencia status) {
        return status == null ? emergenciaRepository.findAll() : emergenciaRepository.findByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoEmergencia> buscarPorId(@PathVariable Long id) {
        return emergenciaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AtendimentoEmergencia> cadastrar(@Valid @RequestBody AtendimentoEmergencia emergencia) {
        Paciente paciente = pacienteRepository.findById(emergencia.getPacienteId())
                .orElseThrow(() -> new RegraDeNegocioException("Paciente não encontrado."));
        Profissional profissional = profissionalRepository.findById(emergencia.getProfissionalId())
                .orElseThrow(() -> new RegraDeNegocioException("Profissional não encontrado."));
        if (!(profissional instanceof Medico)) throw new RegraDeNegocioException("A emergência exige um médico responsável.");
        emergencia.setPaciente(paciente);
        emergencia.setProfissional(profissional);
        emergencia.validar();
        return ResponseEntity.status(HttpStatus.CREATED).body(emergenciaRepository.save(emergencia));
    }
    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<AtendimentoEmergencia> iniciar(@PathVariable Long id) {
        AtendimentoEmergencia emergencia = emergenciaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Atendimento de emergência não encontrado."));
        emergencia.iniciar();
        return ResponseEntity.ok(emergenciaRepository.save(emergencia));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<AtendimentoEmergencia> concluir(@PathVariable Long id) {
        AtendimentoEmergencia emergencia = emergenciaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Atendimento de emergência não encontrado."));
        emergencia.concluir();
        return ResponseEntity.ok(emergenciaRepository.save(emergencia));
    }

}
