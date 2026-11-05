package com.healthtech.hospital.repository;

import com.healthtech.hospital.model.Consulta;
import com.healthtech.hospital.model.enums.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByStatus(StatusConsulta status);

    @Query("select count(c) from ConsultaEspecializada c where c.profissional.id = :profissionalId "
            + "and c.status in :situacoes and lower(c.especialidade) <> lower(:especialidade)")
    long contarDeOutraEspecialidade(@Param("profissionalId") Long profissionalId,
                                    @Param("situacoes") Collection<StatusConsulta> situacoes,
                                    @Param("especialidade") String especialidade);
}
