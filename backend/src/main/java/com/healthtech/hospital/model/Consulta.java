package com.healthtech.hospital.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.enums.StatusConsulta;
import jakarta.persistence.*;
@Entity
public abstract class Consulta extends Atendimento {

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @Column(length = 2000)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String observacoes;

    private void mudarPara(StatusConsulta novo) {
        if (!status.podeIrPara(novo)) {
            throw new RegraDeNegocioException("Não é possível alterar a consulta de " + status + " para " + novo + ".");
        }
        status = novo;
    }
 @Override
    public void confirmar() { mudarPara(StatusConsulta.CONFIRMADA); }

    @Override
    public void iniciar() { mudarPara(StatusConsulta.EM_ATENDIMENTO); }

    @Override
    public void concluir() { mudarPara(StatusConsulta.CONCLUIDA); }

    @Override
    public void cancelar() { mudarPara(StatusConsulta.CANCELADA); }

    @Override
    public void marcarNaoComparecimento() { mudarPara(StatusConsulta.NAO_COMPARECEU); }

    @Override
    public void registrarObservacoes(String observacoes) {
        if (status != StatusConsulta.EM_ATENDIMENTO && status != StatusConsulta.CONCLUIDA) {
            throw new RegraDeNegocioException("As observações só podem ser registradas durante ou após o atendimento.");
        }
        this.observacoes = observacoes;
    }

    public StatusConsulta getStatus() { return status; }
    public String getObservacoes() { return observacoes; }
}