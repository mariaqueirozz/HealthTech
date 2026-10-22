package com.healthtech.hospital.model;

import jakarta.persistence.Entity;

@Entity
public class Cirurgiao extends Medico {

    private String areaCirurgica;

    public String getAreaCirurgica() { return areaCirurgica; }
    public void setAreaCirurgica(String areaCirurgica) { this.areaCirurgica = areaCirurgica; }
}
