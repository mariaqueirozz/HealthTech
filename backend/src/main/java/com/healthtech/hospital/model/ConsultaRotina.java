package com.healthtech.hospital.model;

import com.healthtech.hospital.exception.RegraDeNegocioException;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class ConsultaRotina extends Consulta {


@NotBlank
private String motivo;

@Override
public void validar() {
    validarComum();

    if (!(getProfissional() instanceof Medico)) {
        throw new RegraDeNegocioException("A consulta de rotina exige um médico responsável.");
    }

    if (motivo == null || motivo.isBlank()) {
        throw new RegraDeNegocioException("O motivo da consulta é obrigatório.");
    }
}
public String getMotivo() {
    return motivo;
}

public void setMotivo(String motivo) {
    this.motivo = motivo;
}
}
