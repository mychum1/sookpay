package com.mychum1.sookpay.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="receipt")
public class Receipt {

    @Id
    private String receiptId;

    private String token; //외래키

    private String recipient;

    private Long initDate;

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Long getInitDate() {
        return initDate;
    }

    public void setInitDate(Long initDate) {
        this.initDate = initDate;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "receiptId='" + receiptId + '\'' +
                ", token='" + token + '\'' +
                ", recipient='" + recipient + '\'' +
                ", initDate=" + initDate +
                '}';
    }
}
