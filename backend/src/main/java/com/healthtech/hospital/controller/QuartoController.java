package com.healthtech.hospital.controller;

import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.Quarto;
import com.healthtech.hospital.repository.InternacaoRepository;
import com.healthtech.hospital.repository.QuartoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/quartos")
public class QuartoController {
    private final QuartoRepository quartoRepository;
    private final InternacaoRepository internacaoRepository;

    public QuartoController(QuartoRepository quartoRepository, InternacaoRepository internacaoRepository) {
        this.quartoRepository = quartoRepository; this.internacaoRepository = internacaoRepository;
    }

    @GetMapping public List<Quarto> listar() { return quartoRepository.findAll(); }
    @GetMapping("/{id}") public ResponseEntity<Quarto> buscar(@PathVariable Long id) {
        return quartoRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping public ResponseEntity<Quarto> cadastrar(@Valid @RequestBody Quarto quarto) {
        quarto.validar();
        return ResponseEntity.status(HttpStatus.CREATED).body(quartoRepository.save(quarto));
    }
    @PutMapping("/{id}") @Transactional
    public ResponseEntity<Quarto> atualizar(@PathVariable Long id, @Valid @RequestBody Quarto dados) {
        return quartoRepository.buscarPorIdComBloqueio(id).map(quarto -> {
            long ocupacao = internacaoRepository.countByQuarto_IdAndDataEfetivaAltaIsNull(id);
            if (dados.getCapacidadeMaxima() < ocupacao) {
                throw new RegraDeNegocioException("O quarto possui " + ocupacao + " paciente(s) internado(s); a capacidade máxima não pode ser menor que isso.");
            }
            quarto.setNumero(dados.getNumero()); quarto.setAndar(dados.getAndar());
            quarto.setCapacidadeMaxima(dados.getCapacidadeMaxima()); quarto.setTipo(dados.getTipo());
            quarto.atualizarSituacao(ocupacao); return ResponseEntity.ok(quartoRepository.save(quarto));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!quartoRepository.existsById(id)) return ResponseEntity.notFound().build();
        quartoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
