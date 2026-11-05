package com.healthtech.hospital.controller;

import com.healthtech.hospital.exception.RecursoNaoEncontradoException;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.*;
import com.healthtech.hospital.model.enums.StatusCirurgia;
import com.healthtech.hospital.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Consumer;

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

    /** Filtros opcionais: ?cirurgiaoId=1 e ?status=AGENDADO */
    @GetMapping
    public List<ProcedimentoCirurgico> listar(@RequestParam(required = false) Long cirurgiaoId,
                                              @RequestParam(required = false) StatusCirurgia status) {
        List<ProcedimentoCirurgico> lista;
        if (cirurgiaoId == null) {
            lista = procedimentoRepository.findAll();
        } else {
            Profissional profissional = profissionalRepository.findById(cirurgiaoId)
                    .orElseThrow(() -> new RegraDeNegocioException("Cirurgião não encontrado."));
            if (!(profissional instanceof Cirurgiao)) throw new RegraDeNegocioException("O profissional informado não é um cirurgião.");
            lista = procedimentoRepository.findByProfissional_Id(cirurgiaoId);
        }
        if (status != null) {
            lista = lista.stream().filter(p -> p.getStatus() == status).toList();
        }
        return lista;
    }

    @GetMapping("/cirurgiao/{cirurgiaoId}")
    public List<ProcedimentoCirurgico> listarPorCirurgiao(@PathVariable Long cirurgiaoId) {
        return listar(cirurgiaoId, null);
    }

    @GetMapping("/{id}")
    public ProcedimentoCirurgico buscarPorId(@PathVariable Long id) {
        return buscar(id);
    }

    @PostMapping
    public ResponseEntity<ProcedimentoCirurgico> cadastrar(@Valid @RequestBody ProcedimentoCirurgico procedimento) {
        if (procedimento.getPacienteId() == null || procedimento.getProfissionalId() == null) {
            throw new RegraDeNegocioException("Informe pacienteId e profissionalId.");
        }
        Paciente paciente = pacienteRepository.findById(procedimento.getPacienteId())
                .orElseThrow(() -> new RegraDeNegocioException("Paciente não encontrado."));
        Profissional profissional = profissionalRepository.findById(procedimento.getProfissionalId())
                .orElseThrow(() -> new RegraDeNegocioException("Profissional não encontrado."));
        procedimento.setPaciente(paciente);
        procedimento.setProfissional(profissional);
        procedimento.validar();
        return ResponseEntity.status(HttpStatus.CREATED).body(procedimentoRepository.save(procedimento));
    }

    // ---------- Situação do procedimento ----------

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<ProcedimentoCirurgico> iniciar(@PathVariable Long id) {
        return executar(id, ProcedimentoCirurgico::iniciar);
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ProcedimentoCirurgico> concluir(@PathVariable Long id) {
        return executar(id, ProcedimentoCirurgico::concluir);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ProcedimentoCirurgico> cancelar(@PathVariable Long id) {
        return executar(id, ProcedimentoCirurgico::cancelar);
    }

    /** Body: {"observacoes": "..."} — observações pós-operatórias (só após o início). */
    @PatchMapping("/{id}/observacoes")
    public ResponseEntity<ProcedimentoCirurgico> registrarObservacoes(@PathVariable Long id,
                                                                      @RequestBody ObservacoesRequest dados) {
        if (dados == null || dados.observacoes() == null || dados.observacoes().isBlank()) {
            throw new RegraDeNegocioException("As observações não podem ser vazias.");
        }
        return executar(id, p -> p.registrarObservacoes(dados.observacoes()));
    }

    private ResponseEntity<ProcedimentoCirurgico> executar(Long id, Consumer<ProcedimentoCirurgico> acao) {
        ProcedimentoCirurgico procedimento = buscar(id);
        acao.accept(procedimento);
        return ResponseEntity.ok(procedimentoRepository.save(procedimento));
    }

    private ProcedimentoCirurgico buscar(Long id) {
        return procedimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Procedimento cirúrgico não encontrado."));
    }

    public record ObservacoesRequest(String observacoes) {}
}
