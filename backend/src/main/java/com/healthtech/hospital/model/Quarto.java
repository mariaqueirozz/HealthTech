package com.healthtech.hospital.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.enums.SituacaoQuarto;
import com.healthtech.hospital.model.enums.TipoQuarto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Quarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String numero;

    @NotNull
    private Integer andar;

    @NotNull
    @Min(1)
    private Integer capacidadeMaxima;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoQuarto tipo;

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private SituacaoQuarto situacao = SituacaoQuarto.DISPONIVEL;

    public void validar() {
        int limite = tipo.getCapacidadeMaximaPermitida();
        if (capacidadeMaxima > limite) {
            throw new RegraDeNegocioException("Um quarto do tipo " + tipo + " comporta no máximo " + limite + " paciente(s).");
        }
    }

    public boolean temVaga(long ocupacaoAtual) {
        return ocupacaoAtual < capacidadeMaxima;
    }

    public void atualizarSituacao(long ocupacaoAtual) {
        situacao = ocupacaoAtual >= capacidadeMaxima ? SituacaoQuarto.OCUPADO : SituacaoQuarto.DISPONIVEL;
    }

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public Integer getAndar() { return andar; }
    public void setAndar(Integer andar) { this.andar = andar; }
    public Integer getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(Integer capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }
    public TipoQuarto getTipo() { return tipo; }
    public void setTipo(TipoQuarto tipo) { this.tipo = tipo; }
    public SituacaoQuarto getSituacao() { return situacao; }
}
