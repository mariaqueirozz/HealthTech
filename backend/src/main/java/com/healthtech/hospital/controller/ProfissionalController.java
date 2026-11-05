package com.healthtech.hospital.controller;

import com.healthtech.hospital.model.Profissional;
import com.healthtech.hospital.repository.ProfissionalRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    private final ProfissionalRepository profissionalRepository;

    public ProfissionalController(ProfissionalRepository profissionalRepository) {
        this.profissionalRepository = profissionalRepository;
    }

    @GetMapping
    public List<Profissional> listar() {
        return profissionalRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profissional> buscarPorId(@PathVariable Long id) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);

        return profissional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Profissional> cadastrar(
            @Valid @RequestBody Profissional profissional) {

        Profissional profissionalSalvo =
                profissionalRepository.save(profissional);

        return ResponseEntity.status(HttpStatus.CREATED).body(profissionalSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!profissionalRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        profissionalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
