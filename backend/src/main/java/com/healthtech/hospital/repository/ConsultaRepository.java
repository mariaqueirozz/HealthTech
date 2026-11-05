package com.healthtech.hospital.repository;

import com.healthtech.hospital.model.Consulta;
import com.healthtech.hospital.model.enums.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByStatus(StatusConsulta status);
}
