package com.healthtech.hospital.model.enums;

public enum TipoQuarto {
    ENFERMARIA(6), APARTAMENTO(2), UTI(1);

    private final int capacidadeMaximaPermitida;

    TipoQuarto(int capacidadeMaximaPermitida) {
        this.capacidadeMaximaPermitida = capacidadeMaximaPermitida;
    }

    public int getCapacidadeMaximaPermitida() { return capacidadeMaximaPermitida; }
}