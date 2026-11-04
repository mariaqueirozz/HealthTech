package com.healthtech.hospital.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.healthtech.hospital.model.enums.StatusCirurgia;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ProcedimentoCirurgico extends Atendimento {

    @Column(length = 2000)
    private String descricao;

    private String tipoCirurgia;

    @Column(length = 2000)
    private String observacoesPreOperatorias;

    @Column(length = 2000)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String observacoesPosOperatorias;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dataHoraEfetiva;

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private StatusCirurgia status = StatusCirurgia.AGENDADO;

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipoCirurgia() { return tipoCirurgia; }
    public void setTipoCirurgia(String tipoCirurgia) { this.tipoCirurgia = tipoCirurgia; }
    public String getObservacoesPreOperatorias() { return observacoesPreOperatorias; }
    public void setObservacoesPreOperatorias(String o) { this.observacoesPreOperatorias = o; }
    public String getObservacoesPosOperatorias() { return observacoesPosOperatorias; }
    public LocalDateTime getDataHoraEfetiva() { return dataHoraEfetiva; }
    public StatusCirurgia getStatus() { return status; }
}
    @Override
    public void validar() {
        validarComum();
        if (!(getProfissional() instanceof Cirurgiao)) {
            throw new RegraDeNegocioException("O procedimento cirúrgico exige um cirurgião responsável.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new RegraDeNegocioException("A descrição do procedimento é obrigatória.");
        }
        if (tipoCirurgia == null || tipoCirurgia.isBlank()) {
            throw new RegraDeNegocioException("O tipo de cirurgia é obrigatório.");
        }
    }

    @Override
    public void iniciar() {
        if (status != StatusCirurgia.AGENDADO) {
            throw new RegraDeNegocioException("Só é possível iniciar um procedimento agendado.");
        }
        dataHoraEfetiva = LocalDateTime.now();
        status = StatusCirurgia.EM_ANDAMENTO;
    }

    @Override
    public void concluir() {
        if (status != StatusCirurgia.EM_ANDAMENTO) {
            throw new RegraDeNegocioException("Não é permitido concluir um procedimento que não foi iniciado.");
        }
        status = StatusCirurgia.CONCLUIDO;
    }

    @Override
    public void cancelar() {
        if (status == StatusCirurgia.CONCLUIDO || status == StatusCirurgia.CANCELADO) {
            throw new RegraDeNegocioException("Um procedimento " + status + " não pode ser cancelado.");
        }
        status = StatusCirurgia.CANCELADO;
    }