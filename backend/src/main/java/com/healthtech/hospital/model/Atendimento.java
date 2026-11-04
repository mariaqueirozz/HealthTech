package com.healthtech.hospital.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConsultaRotina.class, name = "CONSULTA_ROTINA"),
        @JsonSubTypes.Type(value = ConsultaEspecializada.class, name = "CONSULTA_ESPECIALIZADA"),
        @JsonSubTypes.Type(value = AtendimentoEmergencia.class, name = "EMERGENCIA"),
        @JsonSubTypes.Type(value = ProcedimentoCirurgico.class, name = "CIRURGIA")
})
