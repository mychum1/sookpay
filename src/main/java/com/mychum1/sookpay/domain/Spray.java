package com.mychum1.sookpay.domain;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="spray")
public class Spray {

    @Id
    private String token;

    private String requester;

    private String roomId;

    private Long amountOfMondey;

    private Integer personnel;

    private Long initDate;

    @OneToMany(mappedBy = "receiptId", fetch = FetchType.EAGER) //m+1 문제
    private List<Receipt> receiptList;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Long getAmountOfMondey() {
        return amountOfMondey;
    }

    public void setAmountOfMondey(Long amountOfMondey) {
        this.amountOfMondey = amountOfMondey;
    }

    public Integer getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Integer personnel) {
        this.personnel = personnel;
    }

    public Long getInitDate() {
        return initDate;
    }

    public void setInitDate(Long initDate) {
        this.initDate = initDate;
    }

    public List<Receipt> getReceiptList() {
        return receiptList;
    }

    public void setReceiptList(List<Receipt> receiptList) {
        this.receiptList = receiptList;
    }

    @Override
    public String toString() {
        return "Spray{" +
                "token='" + token + '\'' +
                ", requester='" + requester + '\'' +
                ", roomId='" + roomId + '\'' +
                ", amountOfMondey=" + amountOfMondey +
                ", personnel=" + personnel +
                ", initDate=" + initDate +
                ", receiptList=" + receiptList +
                '}';
    }
}
