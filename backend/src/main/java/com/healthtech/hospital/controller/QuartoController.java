package com.healthtech.hospital.controller;

import com.healthtech.hospital.model.Quarto;
import com.healthtech.hospital.repository.QuartoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/quartos")
public class QuartoController {
    private final QuartoRepository quartoRepository;
    public QuartoController(QuartoRepository quartoRepository) { this.quartoRepository = quartoRepository; }

    @GetMapping public List<Quarto> listar() { return quartoRepository.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<Quarto> buscar(@PathVariable Long id) {
        return quartoRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping public ResponseEntity<Quarto> cadastrar(@Valid @RequestBody Quarto quarto) {
        quarto.validar();
        return ResponseEntity.status(HttpStatus.CREATED).body(quartoRepository.save(quarto));
    }
    @PutMapping("/{id}") public ResponseEntity<Quarto> atualizar(@PathVariable Long id, @Valid @RequestBody Quarto dados) {
        return quartoRepository.findById(id).map(quarto -> {
            quarto.setNumero(dados.getNumero()); quarto.setAndar(dados.getAndar());
            quarto.setCapacidadeMaxima(dados.getCapacidadeMaxima()); quarto.setTipo(dados.getTipo());
            quarto.validar(); return ResponseEntity.ok(quartoRepository.save(quarto));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
