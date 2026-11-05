package com.healthtech.hospital.repository;

import com.healthtech.hospital.model.Quarto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface QuartoRepository extends JpaRepository<Quarto, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select q from Quarto q where q.id = :id")
    Optional<Quarto> buscarPorIdComBloqueio(@Param("id") Long id);
}
