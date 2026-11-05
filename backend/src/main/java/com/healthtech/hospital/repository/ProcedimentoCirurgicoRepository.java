package com.healthtech.hospital.repository;

import com.healthtech.hospital.model.ProcedimentoCirurgico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProcedimentoCirurgicoRepository extends JpaRepository<ProcedimentoCirurgico, Long> {
    List<ProcedimentoCirurgico> findByProfissional_Id(Long cirurgiaoId);
}
