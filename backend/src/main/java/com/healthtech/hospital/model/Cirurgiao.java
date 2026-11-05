package com.healthtech.hospital.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Cirurgiao extends Medico {

    @NotBlank(message = "A área cirúrgica é obrigatória.")
    private String areaCirurgica;

    public String getAreaCirurgica() { return areaCirurgica; }
    public void setAreaCirurgica(String areaCirurgica) { this.areaCirurgica = areaCirurgica; }
}
