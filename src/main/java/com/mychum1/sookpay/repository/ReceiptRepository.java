package com.mychum1.sookpay.repository;

import com.mychum1.sookpay.domain.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {


    @Modifying
    @Query("update Receipt r set r.recipient=:recipient, r.status=true, r.initDate=:initDate " +
            "where r.receiptId=:receiptId and r.status=false " +
            "and " +
            "(select count(re) from Receipt re " +
            "where re.recipient=:recipient and re.token=:token and re.roomId=:roomId) <= 0")
    Integer updateReceiptentAndStatus(String recipient, Long initDate, String token, String roomId, Integer receiptId);


}
