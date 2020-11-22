package com.mychum1.sookpay.repository;

import com.mychum1.sookpay.domain.Receipt;
import com.mychum1.sookpay.domain.Spray;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {


    @EntityGraph(attributePaths = {"spray"})
    List<Receipt> findByTokenAndRoomId(String token, String roomId);

    @Modifying
    @Query("update Receipt r set r.status=:status where r.receiptId=:id")
    void updateStatusById(boolean status, Integer id);

    /**
     * 방번호와 토큰을 가지고 조회한 받은 사람 목록에서 요청한 시간 이전에 받은 사람들 조회
     *
     * @param token
     * @return
     */
    List<Receipt> findByTokenOrderByReceiptOrderAsc(String token);

    @Modifying
    @Query("update Receipt r set r.recipient=:recipient, r.status=true, r.initDate=:initDate " +
            "where r.receiptId=:receiptId and r.status=false " +
            "and " +
            "(select count(re) from Receipt re " +
            "where re.recipient=:recipient and re.token=:token and re.roomId=:roomId) <= 0")
    Integer updateReceiptentAndStatus(String recipient, Long initDate, String token, String roomId, Integer receiptId);


}
