package com.healthtech.hospital.controller;

import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.Cirurgiao;
import com.healthtech.hospital.model.ProcedimentoCirurgico;
import com.healthtech.hospital.model.Profissional;
import com.healthtech.hospital.model.enums.StatusCirurgia;
import com.healthtech.hospital.repository.ProcedimentoCirurgicoRepository;
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
    private final ProcedimentoCirurgicoRepository procedimentoRepository;

    public ProfissionalController(ProfissionalRepository profissionalRepository,
                                  ProcedimentoCirurgicoRepository procedimentoRepository) {
        this.profissionalRepository = profissionalRepository;
        this.procedimentoRepository = procedimentoRepository;
    }

    /** Ex.: GET /profissionais?especialidade=Cardiologia lista só os especialistas daquela área. */
    @GetMapping
    public List<Profissional> listar(@RequestParam(required = false) String especialidade) {
        if (especialidade == null || especialidade.isBlank()) {
            return profissionalRepository.findAll();
        }
        return profissionalRepository.buscarEspecialistasPorArea(especialidade.trim());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profissional> buscarPorId(@PathVariable Long id) {
        Optional<Profissional> profissional = profissionalRepository.findById(id);

        return profissional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Procedimentos do cirurgião (data, paciente, descrição). Filtro opcional: ?status=CONCLUIDO */
    @GetMapping("/{id}/procedimentos")
    public ResponseEntity<List<ProcedimentoCirurgico>> procedimentos(
            @PathVariable Long id,
            @RequestParam(required = false) StatusCirurgia status) {

        Optional<Profissional> profissional = profissionalRepository.findById(id);
        if (profissional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!(profissional.get() instanceof Cirurgiao)) {
            throw new RegraDeNegocioException("Apenas cirurgiões possuem procedimentos cirúrgicos.");
        }

        List<ProcedimentoCirurgico> lista = procedimentoRepository.findByProfissional_Id(id);
        if (status != null) {
            lista = lista.stream().filter(p -> p.getStatus() == status).toList();
        }
        return ResponseEntity.ok(lista);
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
