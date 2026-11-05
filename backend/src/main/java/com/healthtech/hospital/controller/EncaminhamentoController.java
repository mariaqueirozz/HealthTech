package com.healthtech.hospital.controller;

import com.healthtech.hospital.model.*;
import com.healthtech.hospital.repository.*;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/profissionais")
public class EncaminhamentoController {
    private final ProfissionalRepository profissionalRepository;
    private final PacienteRepository pacienteRepository;
    private final ConsultaRepository consultaRepository;

    public EncaminhamentoController(ProfissionalRepository profissionalRepository, PacienteRepository pacienteRepository, ConsultaRepository consultaRepository) {
        this.profissionalRepository = profissionalRepository;
        this.pacienteRepository = pacienteRepository;
        this.consultaRepository = consultaRepository;
    }

    @PostMapping("/{clinicoId}/encaminhamentos")
    public ResponseEntity<ConsultaEspecializada> encaminhar(@PathVariable Long clinicoId, @RequestBody EncaminhamentoRequest dados) {
        Profissional profissional = profissionalRepository.findById(clinicoId)
                .orElseThrow(() -> new RegraDeNegocioException("Clínico geral não encontrado."));
        if (!(profissional instanceof MedicoClinicoGeral clinico)) {
            throw new RegraDeNegocioException("O profissional informado não é um clínico geral.");
        }
        Paciente paciente = pacienteRepository.findById(dados.pacienteId())
                .orElseThrow(() -> new RegraDeNegocioException("Paciente não encontrado."));
        Profissional profissionalEspecialista = profissionalRepository.findById(dados.especialistaId())
                .orElseThrow(() -> new RegraDeNegocioException("Médico especialista não encontrado."));
        if (!(profissionalEspecialista instanceof MedicoEspecialista especialista)) {
            throw new RegraDeNegocioException("O profissional indicado não é um médico especialista.");
        }
        LocalDateTime dataHora = dados.dataHora() == null ? LocalDateTime.now().plusDays(1) : dados.dataHora();
        ConsultaEspecializada consulta = clinico.encaminhar(paciente, especialista, dataHora);
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaRepository.save(consulta));
    }

    public record EncaminhamentoRequest(Long pacienteId, Long especialistaId, LocalDateTime dataHora) {}
}
