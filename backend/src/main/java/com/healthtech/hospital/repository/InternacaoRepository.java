package com.healthtech.hospital.repository;

import com.healthtech.hospital.model.Internacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {
    long countByQuarto_IdAndDataEfetivaAltaIsNull(Long quartoId);
    boolean existsByPaciente_IdAndDataEfetivaAltaIsNull(Long pacienteId);
    boolean existsByPaciente_IdAndDataEfetivaAltaIsNullAndIdNot(Long pacienteId, Long id);
}
