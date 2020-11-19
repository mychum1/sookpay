package com.mychum1.sookpay.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="receipt")
public class Receipt implements Serializable {

    @Id
    @GeneratedValue
    private Integer receiptId;

    @ManyToOne
    @JoinColumn(referencedColumnName = "token", name="token",insertable = false, updatable = false)
    private Spray spray;

    private String token; //외래키호

    private String roomId;

    private String recipient;

    private Long money;

    private Long initDate;

    private Boolean status;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public boolean isSameRecipient(String recipient) {
        return recipient.equals(recipient);
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Spray getSpray() {
        return spray;
    }

    public void setSpray(Spray spray) {
        this.spray = spray;
    }

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public Integer getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Integer receiptId) {
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
                "receiptId=" + receiptId +
                ", spray=" + spray +
                ", token='" + token + '\'' +
                ", roomId='" + roomId + '\'' +
                ", recipient='" + recipient + '\'' +
                ", money=" + money +
                ", initDate=" + initDate +
                ", status=" + status +
                '}';
    }
}
