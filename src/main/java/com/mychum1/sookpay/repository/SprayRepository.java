package com.mychum1.sookpay.repository;

import com.mychum1.sookpay.domain.Spray;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprayRepository extends JpaRepository<Spray, String> {

    Spray findByTokenAndRoomId(String token, String roomId);
}
