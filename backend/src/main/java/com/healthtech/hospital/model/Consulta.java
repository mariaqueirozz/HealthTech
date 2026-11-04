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