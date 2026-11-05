package com.healthtech.hospital.repository;

import com.healthtech.hospital.model.AtendimentoEmergencia;
import com.healthtech.hospital.model.enums.StatusEmergencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AtendimentoEmergenciaRepository extends JpaRepository<AtendimentoEmergencia, Long> {
    List<AtendimentoEmergencia> findByStatus(StatusEmergencia status);
}
