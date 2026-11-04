package com.healthtech.hospital.model;

import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.Consulta;
import com.healthtech.hospital.model.MedicoEspecialista;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;



@Entity
public class ConsultaEspecializada extends Consulta {

@NotBlank
private String especialidade;

@Override
public void validar() {
    validarComum();

    if (especialidade == null || especialidade.isBlank()) {
        throw new RegraDeNegocioException("A especialidade é obrigatória.");
    }

    if (!(getProfissional() instanceof MedicoEspecialista esp) || !esp.atende(especialidade)) {
        throw new RegraDeNegocioException("O profissional responsável deve ser especialista em " + especialidade + ".");
    }
}
    public String getEspecialidade() {
    return especialidade;
}

public void setEspecialidade(String especialidade) {
    this.especialidade = especialidade;
}
}
