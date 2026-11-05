package com.healthtech.hospital.controller;

import com.healthtech.hospital.model.*;
import com.healthtech.hospital.repository.*;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/procedimentos-cirurgicos")
public class ProcedimentoCirurgicoController {
    private final ProcedimentoCirurgicoRepository procedimentoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;

    public ProcedimentoCirurgicoController(ProcedimentoCirurgicoRepository procedimentoRepository, PacienteRepository pacienteRepository, ProfissionalRepository profissionalRepository) {
        this.procedimentoRepository = procedimentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    @GetMapping
    public List<ProcedimentoCirurgico> listar(@RequestParam(required = false) Long cirurgiaoId) {
        if (cirurgiaoId == null) return procedimentoRepository.findAll();
        Profissional profissional = profissionalRepository.findById(cirurgiaoId)
                .orElseThrow(() -> new RegraDeNegocioException("Cirurgião não encontrado."));
        if (!(profissional instanceof Cirurgiao)) throw new RegraDeNegocioException("O profissional informado não é um cirurgião.");
        return procedimentoRepository.findByProfissional_Id(cirurgiaoId);
    }

    @GetMapping("/cirurgiao/{cirurgiaoId}")
    public List<ProcedimentoCirurgico> listarPorCirurgiao(@PathVariable Long cirurgiaoId) {
        return listar(cirurgiaoId);
    }

    @PostMapping
    public ResponseEntity<ProcedimentoCirurgico> cadastrar(@Valid @RequestBody ProcedimentoCirurgico procedimento) {
        Paciente paciente = pacienteRepository.findById(procedimento.getPacienteId())
                .orElseThrow(() -> new RegraDeNegocioException("Paciente não encontrado."));
        Profissional profissional = profissionalRepository.findById(procedimento.getProfissionalId())
                .orElseThrow(() -> new RegraDeNegocioException("Profissional não encontrado."));
        procedimento.setPaciente(paciente);
        procedimento.setProfissional(profissional);
        procedimento.validar();
        return ResponseEntity.status(HttpStatus.CREATED).body(procedimentoRepository.save(procedimento));
    }
}
