package com.healthtech.hospital.controller;

import com.healthtech.hospital.exception.RecursoNaoEncontradoException;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.Consulta;
import com.healthtech.hospital.model.ConsultaEspecializada;
import com.healthtech.hospital.model.ConsultaRotina;
import com.healthtech.hospital.model.Paciente;
import com.healthtech.hospital.model.Profissional;
import com.healthtech.hospital.model.enums.StatusConsulta;
import com.healthtech.hospital.repository.ConsultaRepository;
import com.healthtech.hospital.repository.PacienteRepository;
import com.healthtech.hospital.repository.ProfissionalRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Consumer;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;

    public ConsultaController(
            ConsultaRepository consultaRepository,
            PacienteRepository pacienteRepository,
            ProfissionalRepository profissionalRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.profissionalRepository = profissionalRepository;
    }

    /** Ex.: GET /consultas?status=AGENDADA */
    @GetMapping
    public List<Consulta> listar(@RequestParam(required = false) StatusConsulta status) {
        return status == null ? consultaRepository.findAll() : consultaRepository.findByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consulta> buscarPorId(@PathVariable Long id) {
        return consultaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Consulta> cadastrar(
            @Valid @RequestBody Consulta consulta) {

        associarPacienteEProfissional(consulta);
        consulta.validar();

        Consulta consultaSalva = consultaRepository.save(consulta);

        return ResponseEntity.status(HttpStatus.CREATED).body(consultaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consulta> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Consulta dadosConsulta) {

        Consulta consulta = buscar(id);

        if (consulta.getStatus() != StatusConsulta.AGENDADA && consulta.getStatus() != StatusConsulta.CONFIRMADA) {
            throw new RegraDeNegocioException(
                    "Só é possível editar consultas agendadas ou confirmadas (situação atual: "
                            + consulta.getStatus() + ").");
        }
        if (!consulta.getClass().equals(dadosConsulta.getClass())) {
            throw new RegraDeNegocioException("Não é possível alterar o tipo da consulta.");
        }

        associarPacienteEProfissional(dadosConsulta);

        consulta.setPaciente(dadosConsulta.getPaciente());
        consulta.setProfissional(dadosConsulta.getProfissional());
        consulta.setDataHora(dadosConsulta.getDataHora());

        if (consulta instanceof ConsultaRotina atual && dadosConsulta instanceof ConsultaRotina novos) {
            atual.setMotivo(novos.getMotivo());
        }
        if (consulta instanceof ConsultaEspecializada atual && dadosConsulta instanceof ConsultaEspecializada novos) {
            atual.setEspecialidade(novos.getEspecialidade());
        }

        consulta.validar();

        return ResponseEntity.ok(consultaRepository.save(consulta));
    }

    // ---------- Situação da consulta ----------

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Consulta> confirmar(@PathVariable Long id) {
        return executar(id, Consulta::confirmar);
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<Consulta> iniciar(@PathVariable Long id) {
        return executar(id, Consulta::iniciar);
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Consulta> concluir(@PathVariable Long id) {
        return executar(id, Consulta::concluir);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Consulta> cancelar(@PathVariable Long id) {
        return executar(id, Consulta::cancelar);
    }

    @PatchMapping("/{id}/nao-compareceu")
    public ResponseEntity<Consulta> naoCompareceu(@PathVariable Long id) {
        return executar(id, Consulta::marcarNaoComparecimento);
    }

    /** Body: {"observacoes": "..."} — só durante ou após o atendimento. */
    @PatchMapping("/{id}/observacoes")
    public ResponseEntity<Consulta> registrarObservacoes(@PathVariable Long id,
                                                         @RequestBody ObservacoesRequest dados) {
        if (dados == null || dados.observacoes() == null || dados.observacoes().isBlank()) {
            throw new RegraDeNegocioException("As observações não podem ser vazias.");
        }
        return executar(id, c -> c.registrarObservacoes(dados.observacoes()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!consultaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        consultaRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<Consulta> executar(Long id, Consumer<Consulta> acao) {
        Consulta consulta = buscar(id);
        acao.accept(consulta);
        return ResponseEntity.ok(consultaRepository.save(consulta));
    }

    private Consulta buscar(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada."));
    }

    private void associarPacienteEProfissional(Consulta consulta) {
        if (consulta.getPacienteId() == null || consulta.getProfissionalId() == null) {
            throw new RegraDeNegocioException("Informe pacienteId e profissionalId.");
        }

        Paciente paciente = pacienteRepository.findById(consulta.getPacienteId())
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Paciente não encontrado."));

        Profissional profissional = profissionalRepository.findById(consulta.getProfissionalId())
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Profissional não encontrado."));

        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
    }

    public record ObservacoesRequest(String observacoes) {}
}
