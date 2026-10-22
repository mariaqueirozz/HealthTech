package com.healthtech.hospital.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class MedicoEspecialista extends Medico {

    @NotBlank
    private String especialidade;

    public boolean atende(String especialidadeSolicitada) {
        return especialidade.equalsIgnoreCase(especialidadeSolicitada.trim());
    }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
}