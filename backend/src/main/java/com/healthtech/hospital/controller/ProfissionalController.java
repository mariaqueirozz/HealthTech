package com.healthtech.hospital.controller;

import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.Cirurgiao;
import com.healthtech.hospital.model.MedicoEspecialista;
import com.healthtech.hospital.model.ProcedimentoCirurgico;
import com.healthtech.hospital.model.Profissional;
import com.healthtech.hospital.model.enums.StatusCirurgia;
import com.healthtech.hospital.model.enums.StatusConsulta;
import com.healthtech.hospital.repository.ConsultaRepository;
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
    private final ConsultaRepository consultaRepository;

    public ProfissionalController(ProfissionalRepository profissionalRepository,
                                  ProcedimentoCirurgicoRepository procedimentoRepository,
                                  ConsultaRepository consultaRepository) {
        this.profissionalRepository = profissionalRepository;
        this.procedimentoRepository = procedimentoRepository;
        this.consultaRepository = consultaRepository;
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

    @PutMapping("/{id}")
    public ResponseEntity<Profissional> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Profissional dados) {

        Optional<Profissional> encontrado = profissionalRepository.findById(id);
        if (encontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Profissional profissional = encontrado.get();
        if (!profissional.getClass().equals(dados.getClass())) {
            throw new RegraDeNegocioException("Não é possível alterar o tipo do profissional.");
        }

        profissional.setNome(dados.getNome());
        profissional.setRegistroProfissional(dados.getRegistroProfissional());
        profissional.setTelefone(dados.getTelefone());
        profissional.setEmail(dados.getEmail());

        if (profissional instanceof MedicoEspecialista atual && dados instanceof MedicoEspecialista novos) {
            long consultasEmAberto = consultaRepository.contarDeOutraEspecialidade(
                    id,
                    List.of(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA, StatusConsulta.EM_ATENDIMENTO),
                    novos.getEspecialidade());
            if (consultasEmAberto > 0) {
                throw new RegraDeNegocioException("O especialista possui " + consultasEmAberto
                        + " consulta(s) em aberto em outra especialidade.");
            }
            atual.setEspecialidade(novos.getEspecialidade());
        }
        if (profissional instanceof Cirurgiao atual && dados instanceof Cirurgiao novos) {
            atual.setAreaCirurgica(novos.getAreaCirurgica());
        }

        return ResponseEntity.ok(profissionalRepository.save(profissional));
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
