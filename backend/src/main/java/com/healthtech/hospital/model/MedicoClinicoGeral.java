package com.healthtech.hospital.model;

import com.healthtech.hospital.exception.RegraDeNegocioException;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class MedicoClinicoGeral extends Medico {

    public ConsultaEspecializada encaminhar(Paciente paciente, MedicoEspecialista especialista, LocalDateTime dataHora) {
        ConsultaEspecializada consulta = new ConsultaEspecializada();
        consulta.setPaciente(paciente);
        consulta.setProfissional(especialista);
        consulta.setDataHora(dataHora);
        consulta.setEspecialidade(especialista.getEspecialidade());
        consulta.validar();
        return consulta;
    }

    public void registrarConsulta(Consulta consulta, String observacoes) {
        if (consulta.getProfissional() == null || !getId().equals(consulta.getProfissional().getId())) {
            throw new RegraDeNegocioException("Esta consulta não pertence a este profissional.");
        }
        consulta.registrarObservacoes(observacoes);
    }
}
