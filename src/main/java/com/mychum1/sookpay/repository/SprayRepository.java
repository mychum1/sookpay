package com.mychum1.sookpay.repository;

import com.mychum1.sookpay.domain.Spray;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SprayRepository extends JpaRepository<Spray, Integer> {

    // r where r.token=:token
    @Query("select s from Spray s join fetch s.receiptList where s.token=:token")
    Spray findByToken(String token);

    Boolean existsByToken(String token);
}
