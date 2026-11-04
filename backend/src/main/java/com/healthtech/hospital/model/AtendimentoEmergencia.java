package com.healthtech.hospital.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.enums.PrioridadeEmergencia;
import com.healthtech.hospital.model.enums.StatusEmergencia;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class AtendimentoEmergencia extends Atendimento {

    @Enumerated(EnumType.STRING)
    private PrioridadeEmergencia prioridade;

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private StatusEmergencia status = StatusEmergencia.AGUARDANDO;

    @Override
    public void validar() {
        if (getDataHora() == null) setDataHora(LocalDateTime.now());
        validarComum();
        if (prioridade == null) {
            throw new RegraDeNegocioException("A prioridade do atendimento de emergência é obrigatória.");
        }
        if (!(getProfissional() instanceof Medico)) {
            throw new RegraDeNegocioException("O atendimento de emergência exige um médico responsável.");
        }
    }

    @Override
    public void iniciar() {
        if (status != StatusEmergencia.AGUARDANDO) {
            throw new RegraDeNegocioException("Só é possível iniciar um atendimento que está aguardando avaliação.");
        }
        status = StatusEmergencia.INICIADO;
    }

    @Override
    public void concluir() {
        if (status != StatusEmergencia.INICIADO) {
            throw new RegraDeNegocioException("Só é possível concluir um atendimento que já foi iniciado.");
        }
        status = StatusEmergencia.CONCLUIDO;
    }

    public PrioridadeEmergencia getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeEmergencia prioridade) { this.prioridade = prioridade; }
    public StatusEmergencia getStatus() { return status; }
}
