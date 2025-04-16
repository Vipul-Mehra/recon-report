package com.repository;

import com.entity.EleoxOxNomTradeExtract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EleoxOxNomTradeExtractRepository extends JpaRepository<EleoxOxNomTradeExtract, Long> {

    @Query("SELECT e.etrmDealId FROM EleoxOxNomTradeExtract e WHERE e.trdStartDate >= :startDate AND e.trdStartDate <= :endDate")
    List<Long> findAllDealIds(LocalDate startDate, LocalDate endDate);
}