package com.mychum1.sookpay.domain;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name="spray")
public class Spray implements Serializable {

    //TODO 변수명 다시 고민해볼 것
    @Id
    @GeneratedValue
    private Integer id;

    @NaturalId
    private String token;

    private String requester;

    private String roomId;

    private Long amountOfMondey;

    private Integer personnel;

    private Long initDate;

    public Spray(String token, String requester, String roomId, Long amountOfMondey, Integer personnel, Long initDate) {
        this.token = token;
        this.requester = requester;
        this.roomId = roomId;
        this.amountOfMondey = amountOfMondey;
        this.personnel = personnel;
        this.initDate = initDate;
    }

    public Spray() {}

    public boolean isValidRequester(String requester) {
        return !this.getRequester().equals(requester);
    }

    public boolean isValidTime(Long duration) {

        return this.initDate + duration > Instant.now().getEpochSecond();
    }

    //m+1 문제
//    @OneToMany
//    @JoinColumn(name = "token")
//    private List<Receipt> receiptList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

//    public List<Receipt> getReceiptList() {
//        return receiptList;
//    }
//
//    public void setReceiptList(List<Receipt> receiptList) {
//        this.receiptList = receiptList;
//    }

    @Override
    public String toString() {
        return "Spray{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", requester='" + requester + '\'' +
                ", roomId='" + roomId + '\'' +
                ", amountOfMondey=" + amountOfMondey +
                ", personnel=" + personnel +
                ", initDate=" + initDate +
//                ", receiptList=" + receiptList +
                '}';
    }
}
