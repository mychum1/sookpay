package com.mychum1.sookpay.domain;

import java.util.List;

public class SprayInfo {
    private Long initDate;
    private Long amountOfMoney;
    private Long totalReceived;
    private List<ReceivedInfo> receivedInfoList;

    public static class ReceivedInfo {
        String recipient;
        Long receivedMoney;

        public ReceivedInfo() {
        }

        public ReceivedInfo(String recipient, Long receivedMoney) {
            this.recipient = recipient;
            this.receivedMoney = receivedMoney;
        }

        public String getRecipient() {
            return recipient;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }

        public Long getReceivedMoney() {
            return receivedMoney;
        }

        public void setReceivedMoney(Long receivedMoney) {
            this.receivedMoney = receivedMoney;
        }

        @Override
        public String toString() {
            return "ReceivedInfo{" +
                    "recipient='" + recipient + '\'' +
                    ", receivedMoney=" + receivedMoney +
                    '}';
        }
    }

    public Long getInitDate() {
        return initDate;
    }

    public void setInitDate(Long initDate) {
        this.initDate = initDate;
    }

    public Long getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Long amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }

    public Long getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(Long totalReceived) {
        this.totalReceived = totalReceived;
    }

    public List<ReceivedInfo> getReceivedInfoList() {
        return receivedInfoList;
    }

    public void setReceivedInfoList(List<ReceivedInfo> receivedInfoList) {
        this.receivedInfoList = receivedInfoList;
    }

    @Override
    public String toString() {
        return "SprayInfo{" +
                "initDate=" + initDate +
                ", amountOfMoney=" + amountOfMoney +
                ", totalReceived=" + totalReceived +
                ", receivedInfoList=" + receivedInfoList +
                '}';
    }
}
