package com.abes.defenceprocurement.contract;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContractStatusRepository extends JpaRepository<ContractStatus, Long> {
    Optional<ContractStatus> findByCode(String code);

    @Query("select new com.abes.defenceprocurement.contract.ContractStatusOption(s.code, s.name) " +
            "from ContractStatus s order by s.name")
    List<ContractStatusOption> findAllOptions();
}
