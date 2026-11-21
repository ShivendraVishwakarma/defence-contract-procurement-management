package com.abes.defenceprocurement.contract;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    boolean existsByContractNumber(String contractNumber);

    boolean existsByContractNumberAndIdNot(String contractNumber, Long id);

    @Query("""
            select new com.abes.defenceprocurement.contract.ContractSummary(
                c.id, c.contractNumber, c.title, v.id, v.name, s.code, s.name,
                c.startDate, c.endDate, c.contractValue, c.summary)
            from Contract c
            join c.vendor v
            join c.status s
            order by c.contractNumber
            """)
    List<ContractSummary> findAllForListing();

    @Query("""
            select new com.abes.defenceprocurement.contract.ContractSummary(
                c.id, c.contractNumber, c.title, v.id, v.name, s.code, s.name,
                c.startDate, c.endDate, c.contractValue, c.summary)
            from Contract c
            join c.vendor v
            join c.status s
            where c.id = :id
            """)
    Optional<ContractSummary> findSummaryById(@Param("id") Long id);
}
