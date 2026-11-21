package com.abes.defenceprocurement.contract;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    @Query("select new com.abes.defenceprocurement.contract.VendorOption(v.id, v.code, v.name) " +
            "from Vendor v order by v.name")
    List<VendorOption> findAllOptions();
}
