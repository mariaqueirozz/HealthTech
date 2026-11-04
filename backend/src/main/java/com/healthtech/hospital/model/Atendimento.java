package com.healthtech.hospital.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConsultaRotina.class, name = "CONSULTA_ROTINA"),
        @JsonSubTypes.Type(value = ConsultaEspecializada.class, name = "CONSULTA_ESPECIALIZADA"),
        @JsonSubTypes.Type(value = AtendimentoEmergencia.class, name = "EMERGENCIA"),
        @JsonSubTypes.Type(value = ProcedimentoCirurgico.class, name = "CIRURGIA")
})
public abstract class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Paciente paciente;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Profissional profissional;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long pacienteId;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long profissionalId;

    private LocalDateTime dataHora;

    public abstract void validar();

    protected void validarComum() {
        if (paciente == null) throw new RegraDeNegocioException("Paciente é obrigatório.");
        if (dataHora == null) throw new RegraDeNegocioException("Data e horário são obrigatórios.");
    }

    public void confirmar() { throw naoSuportada("confirmar"); }
    public void iniciar() { throw naoSuportada("iniciar"); }
    public void concluir() { throw naoSuportada("concluir"); }
    public void cancelar() { throw naoSuportada("cancelar"); }
    public void marcarNaoComparecimento() { throw naoSuportada("marcar não comparecimento"); }
    public void registrarObservacoes(String observacoes) { throw naoSuportada("registrar observações"); }
