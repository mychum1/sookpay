package com.mychum1.sookpay.repository;

import com.mychum1.sookpay.domain.Receipt;
import com.mychum1.sookpay.domain.Spray;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, String> {


    @EntityGraph
    List<Receipt> findByToken(String token);

    /**
     * 방번호와 토큰을 가지고 조회한 받은 사람 목록에서 요청한 시간 이전에 받은 사람들 조회
     *
     * @param token
     * @param roomId
     * @param now
     * @return
     */
    List<Receipt> findByTokenAndRoomIdAndInitDateLessThan(String token, String roomId, Long now);
}
