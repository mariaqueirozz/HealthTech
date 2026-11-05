
package com.healthtech.hospital.controller;

import com.healthtech.hospital.model.Consulta;
import com.healthtech.hospital.model.Paciente;
import com.healthtech.hospital.model.Profissional;
import com.healthtech.hospital.repository.ConsultaRepository;
import com.healthtech.hospital.repository.PacienteRepository;
import com.healthtech.hospital.repository.ProfissionalRepository;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping
    public List<Consulta> listar() {
        return consultaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consulta> buscarPorId(@PathVariable Long id) {
        Optional<Consulta> consulta = consultaRepository.findById(id);

        return consulta.map(ResponseEntity::ok)
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

        Optional<Consulta> consultaEncontrada = consultaRepository.findById(id);

        if (consultaEncontrada.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        associarPacienteEProfissional(dadosConsulta);

        Consulta consulta = consultaEncontrada.get();
        consulta.setPaciente(dadosConsulta.getPaciente());
        consulta.setProfissional(dadosConsulta.getProfissional());
        consulta.setDataHora(dadosConsulta.getDataHora());

        consulta.validar();

        Consulta consultaAtualizada = consultaRepository.save(consulta);

        return ResponseEntity.ok(consultaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!consultaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        consultaRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private void associarPacienteEProfissional(Consulta consulta) {
        Paciente paciente = pacienteRepository.findById(consulta.getPacienteId())
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Paciente não encontrado."));

        Profissional profissional = profissionalRepository.findById(consulta.getProfissionalId())
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Profissional não encontrado."));

        consulta.setPaciente(paciente);
        consulta.setProfissional(profissional);
    }
}
