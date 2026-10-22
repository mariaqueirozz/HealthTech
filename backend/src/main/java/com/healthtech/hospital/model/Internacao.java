package com.healthtech.hospital.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Paciente paciente;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Profissional profissional;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Quarto quarto;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long pacienteId;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long profissionalId;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long quartoId;

    private LocalDate dataEntrada;
    private LocalDate dataPrevistaAlta;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate dataEfetivaAlta;

    @Column(length = 2000)
    private String observacoes;

    public void validar() {
        if (paciente == null) throw new RegraDeNegocioException("Paciente é obrigatório.");
        if (!(profissional instanceof Medico)) throw new RegraDeNegocioException("A internação exige um médico responsável.");
        if (quarto == null) throw new RegraDeNegocioException("Quarto é obrigatório.");
        if (dataEntrada == null) throw new RegraDeNegocioException("A data de entrada é obrigatória.");
        if (dataPrevistaAlta != null && dataPrevistaAlta.isBefore(dataEntrada)) {
            throw new RegraDeNegocioException("A data prevista de alta não pode ser anterior à entrada.");
        }
    }

    public void registrarAlta(LocalDate data) {
        if (dataEfetivaAlta != null) throw new RegraDeNegocioException("Esta internação já teve alta.");
        if (data == null || data.isBefore(dataEntrada)) {
            throw new RegraDeNegocioException("A data de alta não pode ser anterior à entrada.");
        }
        dataEfetivaAlta = data;
    }

    public boolean isAtiva() { return dataEfetivaAlta == null; }

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
    public Profissional getProfissional() { return profissional; }
    public void setProfissional(Profissional profissional) { this.profissional = profissional; }
    public Quarto getQuarto() { return quarto; }
    public void setQuarto(Quarto quarto) { this.quarto = quarto; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public Long getProfissionalId() { return profissionalId; }
    public void setProfissionalId(Long profissionalId) { this.profissionalId = profissionalId; }
    public Long getQuartoId() { return quartoId; }
    public void setQuartoId(Long quartoId) { this.quartoId = quartoId; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }
    public LocalDate getDataPrevistaAlta() { return dataPrevistaAlta; }
    public void setDataPrevistaAlta(LocalDate dataPrevistaAlta) { this.dataPrevistaAlta = dataPrevistaAlta; }
    public LocalDate getDataEfetivaAlta() { return dataEfetivaAlta; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}