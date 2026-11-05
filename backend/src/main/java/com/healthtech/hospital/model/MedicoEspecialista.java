package com.healthtech.hospital.model;

import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class MedicoEspecialista extends Medico {

    @NotBlank
    private String especialidade;

    public boolean atende(String especialidadeSolicitada) {
        return especialidade != null
                && especialidadeSolicitada != null
                && !especialidade.trim().isEmpty()
                && !especialidadeSolicitada.trim().isEmpty()
                && especialidade.equalsIgnoreCase(especialidadeSolicitada.trim());
    }

    public void validar() {
        if (especialidade == null || especialidade.isBlank()) {
            throw new RegraDeNegocioException("A especialidade do médico é obrigatória.");
        }
    }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
}