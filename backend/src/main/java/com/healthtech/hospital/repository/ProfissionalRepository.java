package com.healthtech.hospital.repository;

import com.healthtech.hospital.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {
}