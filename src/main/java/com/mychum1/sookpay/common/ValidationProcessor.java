package com.mychum1.sookpay.common;

import com.mychum1.sookpay.domain.Receipt;
import com.mychum1.sookpay.domain.Spray;
import com.mychum1.sookpay.exception.NotValidSprayException;

import java.util.List;

/**
 * 유효성 검사
 */
public class ValidationProcessor {

    /**
     * 이미 전부 받은 뿌리기 건인지 확인한다.
     * @param result
     * @throws NotValidSprayException
     */
    public static void checkAllTaken(Receipt result) throws NotValidSprayException {
        if(result==null) {throw new NotValidSprayException(Code.FAIL_ALL_TAKEN, Code.FAIL_ALL_TAKEN_MSG);}
    }

    /**
     * 조회할 수 있는 요청자의 요청인지 확인한다.
     * @param userId
     * @param spray
     * @throws NotValidSprayException
     */
    public static void isOwnerOfSpray(String userId, Spray spray) throws NotValidSprayException {
        if(!spray.getRequester().equals(userId)) {
            throw new NotValidSprayException(Code.FAIL_NO_AUTHORITY, Code.FAIL_NO_AUTHORITY_MSG);
        }
    }

    /**
     * 유효한 뿌리기 건이 있는지 확인한다.
     * @param receiptList
     * @throws NotValidSprayException
     */
    public static void isValidSpray(Spray spray) throws NotValidSprayException {
        if(spray==null) {
            throw new NotValidSprayException(Code.FAIL_NOT_VALID, Code.FAIL_NOT_VALID_MSG);
        }
    }

    /**
     * 유효시간 내의 요청인지 확인한다.
     * @param spray
     * @param duration
     * @throws NotValidSprayException
     */
    public static void isValidTime(Spray spray, Long duration) throws NotValidSprayException {
        if(!spray.isValidTime(duration)) {
            throw new NotValidSprayException(Code.FAIL_TIME_OVER, Code.FAIL_TIME_OVER_MSG);
        }
    }

    /**
     * 같은 방의 사용자인지 확인한다.
     * @param receipts
     * @throws NotValidSprayException
     */
    public static void isSameRoom(Spray spray, String roomId) throws NotValidSprayException {
        if(!spray.getRoomId().equals(roomId)) {
            throw new NotValidSprayException(Code.FAIL_NOT_VALID, Code.FAIL_NOT_VALID_MSG);
        }
    }

    /**
     * 이미 받은 사용자인지 확인한다.
     * @param receipts
     * @param userId
     * @throws NotValidSprayException
     */
    public static void isValidRequester(List<Receipt> receipts,String userId) throws NotValidSprayException {
        boolean isValid=true;
        int size = receipts.size();
        for (int i = 0; i < size; i++) {
            if(receipts.get(i).getRecipient().equals(userId)) {
                isValid=false;
                break;
            }
        }
        if(!isValid) {
            throw new NotValidSprayException(Code.FAIL_ALREADY_TAKEN, Code.FAIL_ALREADY_TAKEN_MSG);
        }
    }

    /**
     * 돈을 뿌린 사람과 받기 요청한 사람이 같은지 확인한다.
     * @param spray
     * @param userId
     * @return
     */
    public static void isSameRequester(Spray spray, String userId) throws NotValidSprayException {
        if(!spray.isValidRequester(userId)) {
            throw new NotValidSprayException(Code.FAIL_CODE, "Not Valid");
        }
    }

}
