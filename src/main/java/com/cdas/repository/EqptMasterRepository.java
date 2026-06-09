package com.cdas.repository;

import com.cdas.entity.EqptMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EqptMasterRepository
        extends JpaRepository<EqptMaster, Long> {

    @Query(
            value = "SELECT * FROM eqpt_master WHERE shop_code = ?1",
            nativeQuery = true
    )
    List<EqptMaster> getByShopCode(String shopCode);
}