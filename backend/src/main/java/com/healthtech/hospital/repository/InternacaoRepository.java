package com.healthtech.hospital.repository;

import com.healthtech.hospital.model.Internacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternacaoRepository extends JpaRepository<Internacao, Long> {
    long countByQuarto_IdAndDataEfetivaAltaIsNull(Long quartoId);
}
