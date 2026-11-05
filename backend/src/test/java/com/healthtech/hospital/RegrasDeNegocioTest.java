package com.healthtech.hospital;

import com.healthtech.hospital.exception.RegraDeNegocioException;
import com.healthtech.hospital.model.*;
import com.healthtech.hospital.model.enums.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegrasDeNegocioTest {

    private Paciente pacienteValido() {
        Paciente paciente = new Paciente();
        paciente.setNome("Paciente Teste");
        paciente.setCpf("12345678900");
        return paciente;
    }

    @Test
    void consultaEspecializadaExigeEspecialistaCompativel() {
        MedicoEspecialista especialista = new MedicoEspecialista();
        especialista.setEspecialidade("Cardiologia");

        ConsultaEspecializada consulta = new ConsultaEspecializada();
        consulta.setPaciente(pacienteValido());
        consulta.setProfissional(especialista);
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setEspecialidade("Cardiologia");
        assertDoesNotThrow(consulta::validar);

        consulta.setEspecialidade("Neurologia");
        assertThrows(RegraDeNegocioException.class, consulta::validar);
    }

    @Test
    void consultaNaoPermiteVoltarDeConcluidaParaAgendada() {
        ConsultaRotina consulta = new ConsultaRotina();
        consulta.setPaciente(pacienteValido());
        MedicoClinicoGeral medico = new MedicoClinicoGeral();
        consulta.setProfissional(medico);
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setMotivo("Avaliação geral");
        consulta.validar();
        consulta.confirmar();
        consulta.iniciar();
        consulta.concluir();
        assertThrows(RegraDeNegocioException.class, consulta::confirmar);
    }

    @Test
    void cirurgiaSoPodeSerConcluidaDepoisDeIniciada() {
        Cirurgiao cirurgiao = new Cirurgiao();
        cirurgiao.setAreaCirurgica("Geral");

        ProcedimentoCirurgico cirurgia = new ProcedimentoCirurgico();
        cirurgia.setPaciente(pacienteValido());
        cirurgia.setProfissional(cirurgiao);
        cirurgia.setDataHora(LocalDateTime.now().plusDays(2));
        cirurgia.setDescricao("Procedimento de teste");
        cirurgia.setTipoCirurgia("Geral");
        cirurgia.validar();

        assertThrows(RegraDeNegocioException.class, cirurgia::concluir);
        cirurgia.iniciar();
        cirurgia.concluir();
        assertEquals(StatusCirurgia.CONCLUIDO, cirurgia.getStatus());
    }

    @Test
    void emergenciaExigePrioridadeEControlaAndamento() {
        AtendimentoEmergencia emergencia = new AtendimentoEmergencia();
        emergencia.setPaciente(pacienteValido());
        emergencia.setProfissional(new MedicoClinicoGeral());
        emergencia.setPrioridade(PrioridadeEmergencia.ALTA);
        emergencia.validar();
        emergencia.iniciar();
        emergencia.concluir();
        assertEquals(StatusEmergencia.CONCLUIDO, emergencia.getStatus());
        assertThrows(RegraDeNegocioException.class, emergencia::iniciar);
    }

    @Test
    void quartoRespeitaCapacidadeDoTipo() {
        Quarto quarto = new Quarto();
        quarto.setNumero("101");
        quarto.setAndar(1);
        quarto.setTipo(TipoQuarto.UTI);
        quarto.setCapacidadeMaxima(1);
        assertDoesNotThrow(quarto::validar);
        assertTrue(quarto.temVaga(0));
        quarto.atualizarSituacao(1);
        assertEquals(SituacaoQuarto.OCUPADO, quarto.getSituacao());

        quarto.setCapacidadeMaxima(2);
        assertThrows(RegraDeNegocioException.class, quarto::validar);
    }


}
