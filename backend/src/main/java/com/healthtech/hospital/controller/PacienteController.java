package com.healthtech.hospital.controller;

import com.healthtech.hospital.model.Paciente;
import com.healthtech.hospital.repository.PacienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository pacienteRepository;

    public PacienteController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public List<Paciente> listar() {
        return pacienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);

        return paciente
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Paciente> cadastrar(@Valid @RequestBody Paciente paciente) {
        Paciente pacienteSalvo = pacienteRepository.save(paciente);

        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Paciente dadosPaciente) {

        Optional<Paciente> pacienteEncontrado = pacienteRepository.findById(id);

        if (pacienteEncontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Paciente paciente = pacienteEncontrado.get();
        paciente.setNome(dadosPaciente.getNome());
        paciente.setCpf(dadosPaciente.getCpf());
        paciente.setDataNascimento(dadosPaciente.getDataNascimento());
        paciente.setTelefone(dadosPaciente.getTelefone());
        paciente.setEndereco(dadosPaciente.getEndereco());
        paciente.setEmail(dadosPaciente.getEmail());

        Paciente pacienteAtualizado = pacienteRepository.save(paciente);

        return ResponseEntity.ok(pacienteAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!pacienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        pacienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}