package com.healthtech.hospital.controller;

import com.healthtech.hospital.model.*;
import com.healthtech.hospital.repository.*;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/internacoes")
public class InternacaoController {
    private final InternacaoRepository internacaoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;
    private final QuartoRepository quartoRepository;

    public InternacaoController(InternacaoRepository internacaoRepository, PacienteRepository pacienteRepository, ProfissionalRepository profissionalRepository, QuartoRepository quartoRepository) {
        this.internacaoRepository = internacaoRepository; this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository; this.quartoRepository = quartoRepository;
    }

    @GetMapping public List<Internacao> listar() { return internacaoRepository.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<Internacao> buscar(@PathVariable Long id) {
        return internacaoRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping @Transactional
    public ResponseEntity<Internacao> cadastrar(@RequestBody Internacao internacao) {
        Paciente paciente = pacienteRepository.findById(internacao.getPacienteId())
                .orElseThrow(() -> new RegraDeNegocioException("Paciente não encontrado."));
        Profissional profissional = profissionalRepository.findById(internacao.getProfissionalId())
                .orElseThrow(() -> new RegraDeNegocioException("Profissional não encontrado."));
        Quarto quarto = quartoRepository.buscarPorIdComBloqueio(internacao.getQuartoId())
                .orElseThrow(() -> new RegraDeNegocioException("Quarto não encontrado."));
        internacao.setPaciente(paciente); internacao.setProfissional(profissional); internacao.setQuarto(quarto);
        internacao.validar();
        if (internacaoRepository.existsByPaciente_IdAndDataEfetivaAltaIsNull(paciente.getId())) {
            throw new RegraDeNegocioException("O paciente já possui uma internação ativa.");
        }
        long ocupacao = internacaoRepository.countByQuarto_IdAndDataEfetivaAltaIsNull(quarto.getId());
        if (!quarto.temVaga(ocupacao)) throw new RegraDeNegocioException("O quarto não possui vagas disponíveis.");
        Internacao salva = internacaoRepository.save(internacao);
        quarto.atualizarSituacao(ocupacao + 1); quartoRepository.save(quarto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PutMapping("/{id}") @Transactional
    public ResponseEntity<Internacao> atualizar(@PathVariable Long id, @RequestBody Internacao dados) {
        Internacao internacao = internacaoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Internação não encontrada."));
        if (!internacao.isAtiva()) throw new RegraDeNegocioException("Não é possível editar uma internação que já teve alta.");
        Paciente paciente = pacienteRepository.findById(dados.getPacienteId())
                .orElseThrow(() -> new RegraDeNegocioException("Paciente não encontrado."));
        Profissional profissional = profissionalRepository.findById(dados.getProfissionalId())
                .orElseThrow(() -> new RegraDeNegocioException("Profissional não encontrado."));
        if (internacaoRepository.existsByPaciente_IdAndDataEfetivaAltaIsNullAndIdNot(paciente.getId(), id)) {
            throw new RegraDeNegocioException("O paciente já possui uma internação ativa.");
        }
        Quarto quartoAnterior = quartoRepository.buscarPorIdComBloqueio(internacao.getQuarto().getId())
                .orElseThrow(() -> new RegraDeNegocioException("Quarto não encontrado."));
        Quarto quarto = quartoRepository.buscarPorIdComBloqueio(dados.getQuartoId())
                .orElseThrow(() -> new RegraDeNegocioException("Quarto não encontrado."));
        boolean trocouDeQuarto = !quarto.getId().equals(quartoAnterior.getId());
        if (trocouDeQuarto && !quarto.temVaga(internacaoRepository.countByQuarto_IdAndDataEfetivaAltaIsNull(quarto.getId()))) {
            throw new RegraDeNegocioException("O quarto não possui vagas disponíveis.");
        }
        internacao.setPaciente(paciente); internacao.setProfissional(profissional); internacao.setQuarto(quarto);
        internacao.setDataEntrada(dados.getDataEntrada()); internacao.setDataPrevistaAlta(dados.getDataPrevistaAlta());
        internacao.setObservacoes(dados.getObservacoes());
        internacao.validar();
        Internacao atualizada = internacaoRepository.save(internacao);
        if (trocouDeQuarto) {
            quartoAnterior.atualizarSituacao(internacaoRepository.countByQuarto_IdAndDataEfetivaAltaIsNull(quartoAnterior.getId()));
            quarto.atualizarSituacao(internacaoRepository.countByQuarto_IdAndDataEfetivaAltaIsNull(quarto.getId()));
            quartoRepository.save(quartoAnterior); quartoRepository.save(quarto);
        }
        return ResponseEntity.ok(atualizada);
    }

    @PatchMapping("/{id}/alta") @Transactional
    public ResponseEntity<Internacao> registrarAlta(@PathVariable Long id, @RequestParam(required = false) LocalDate data) {
        Internacao internacao = internacaoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Internação não encontrada."));
        Quarto quarto = quartoRepository.buscarPorIdComBloqueio(internacao.getQuarto().getId())
                .orElseThrow(() -> new RegraDeNegocioException("Quarto não encontrado."));
        internacao.registrarAlta(data == null ? LocalDate.now() : data);
        Internacao atualizada = internacaoRepository.save(internacao);
        long ocupacao = internacaoRepository.countByQuarto_IdAndDataEfetivaAltaIsNull(quarto.getId());
        quarto.atualizarSituacao(ocupacao); quartoRepository.save(quarto);
        return ResponseEntity.ok(atualizada);
    }
}
