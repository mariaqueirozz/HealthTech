package com.healthtech.hospital.repository;

import com.healthtech.hospital.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    @Query("select e from MedicoEspecialista e where lower(e.especialidade) = lower(:especialidade)")
    List<Profissional> buscarEspecialistasPorArea(@Param("especialidade") String especialidade);
}
