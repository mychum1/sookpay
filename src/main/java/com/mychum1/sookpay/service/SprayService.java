package com.mychum1.sookpay.service;

import com.mychum1.sookpay.common.Code;
import com.mychum1.sookpay.common.RandomTokenProcessor;
import com.mychum1.sookpay.domain.Receipt;
import com.mychum1.sookpay.domain.Spray;
import com.mychum1.sookpay.domain.SprayInfo;
import com.mychum1.sookpay.exception.NotValidSprayException;
import com.mychum1.sookpay.repository.ReceiptRepository;
import com.mychum1.sookpay.repository.SprayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class SprayService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SprayRepository sprayRepository;

    @Autowired
    ReceiptRepository receiptRepository;

    @Value("${get.request.duration}")
    private Long getDuration;

    @Value("${get.info.request.duration}")
    private Long getInfoDuration;

    /**
     * 뿌리기 건을 등록하면서 뿌리기를 받을 건들도 등록해둔다.
     * @param requester
     * @param roomId
     * @param amountOfMoney
     * @param personnel
     * @return
     */
    @Transactional
    public Spray postSpray(String requester, String roomId, Long amountOfMoney, Integer personnel) {
        //token 3자리 암호화
        Spray spray = new Spray(RandomTokenProcessor.makeRandomToken(), requester, roomId, amountOfMoney, personnel, Instant.now().getEpochSecond());
        sprayRepository.save(spray);
        //TODO 나누어 떨어지지 않는 금액이 발생
        long money = amountOfMoney/personnel;
        for (int i = 0; i < personnel; i++) {
            Receipt receipt = new Receipt();
            receipt.setSpray(spray);
            receipt.setRoomId(roomId);
            receipt.setToken(spray.getToken());
            receipt.setMoney(money);
            receipt.setStatus(false);
            receipt.setReceiptOrder(i);
            receipt.setRecipient("");
            receiptRepository.save(receipt);
        }

        return spray;
    }

    @Transactional(rollbackOn = {NotValidSprayException.class})
    public Receipt getSpray(String token, String userId, String roomId) throws NotValidSprayException {
        //1. 한 사용자는 한 번만 o
        //2. 받는 금액을 응답값으로
        //3. 동일한 대화방에 속한 사용자만 받을 수 있다. o TODO 방에 있는 사람들을 관리해야 할까? 아니면 방 번호와 토큰을 알면 그 방의 사용자라고 봐도 될까?
        //4. 10분간만 유효 o
        //5. 본인은 받지 못함 o
        List<Receipt> receiptList = receiptRepository.findByTokenAndRoomIdOrderByReceiptOrderAsc(token, roomId);
        isSameRoom(receiptList); //3

        Spray spray = receiptList.get(0).getSpray();
        isValidTime(spray); //4
        isSameRequester(spray, userId); //5
        isValidRequester(receiptList, userId); //1

        int size = receiptList.size();
        Receipt result = null;
        for (int i = 0; i < size; i++) {
            Receipt receipt = receiptList.get(i);
            if(receiptRepository.updateReceiptentAndStatus(userId, Instant.now().getEpochSecond(), token, roomId, receipt.getReceiptId())>0) {
                result = receipt;
               break;
            }
        }
        return result;

    }

    private void isOwnerOfSpray(String userId, Spray spray) throws NotValidSprayException {
        if(!spray.getRequester().equals(userId)) {
            throw new NotValidSprayException(500, "No Authority");
        }
    }

    private void isReadablePeriod(Spray spray, Long duration) throws NotValidSprayException {
        if(!spray.isValidTime(duration)) {
            throw new NotValidSprayException(500, "Time Over");
        }
    }

    private void isValidSpray(List<Receipt> receiptList) throws NotValidSprayException {
        if(receiptList==null || receiptList.size()<=0) {
            throw new NotValidSprayException(500, "No Spray Info");
        }
    }

    public SprayInfo getSprayDetail(String token, String userId, String roomId) throws NotValidSprayException {
        List<Receipt> receiptList = receiptRepository.findByTokenAndRoomId(token, roomId);
        isValidSpray(receiptList);
        Spray spray = receiptList.get(0).getSpray();
        isOwnerOfSpray(userId, spray); // 뿌린사람 확인
        isReadablePeriod(spray, getInfoDuration); //7일간만 조회 가능

        //TODO 양방향으로 mapping 해주어야 할까?

        //뿌린시각, 뿌린 금액, 받기완료된 금액, 받기 완료된 정보[받은 금액, 받은 사람,]

        SprayInfo sprayInfo = new SprayInfo();
        sprayInfo.setInitDate(spray.getInitDate());
        sprayInfo.setAmountOfMoney(spray.getAmountOfMondey());
        Long total=0L;
        int receiptsSize=receiptList.size();
        List<SprayInfo.ReceivedInfo> receivedInfos = new ArrayList<SprayInfo.ReceivedInfo>();
        for (int i = 0; i < receiptsSize; i++) {
            Receipt receipt = receiptList.get(i);
            if(receipt.getStatus()) {
                Long money = receipt.getMoney();
                total += money;
                receivedInfos.add(new SprayInfo.ReceivedInfo(receipt.getRecipient(), money));
            }

        }
        sprayInfo.setTotalReceived(total);
        sprayInfo.setReceivedInfoList(receivedInfos);

        return sprayInfo;

    }

    private void isValidTime(Spray spray) throws NotValidSprayException {
        if(!spray.isValidTime(getDuration)) {
            throw new NotValidSprayException(500, "Time Over");
        }
    }

    private void isSameRoom(List<Receipt> receipts) throws NotValidSprayException {
        if(receipts == null || receipts.size()==0) {
            throw new NotValidSprayException(500, "No Room User");
        }
    }

    private void isValidRequester(List<Receipt> receipts,String userId) throws NotValidSprayException {
        boolean isValid=true;
        int size = receipts.size();
        for (int i = 0; i < size; i++) {
            if(receipts.get(i).getRecipient().equals(userId)) {
                isValid=false;
                break;
            }
        }
        if(!isValid) {
            throw new NotValidSprayException(500, "Already taken");
        }
    }

    /**
     * 돈을 뿌린 사람과 받기 요청한 사람이 같은지 확인한다.
     * @param spray
     * @param userId
     * @return
     */
    private void isSameRequester(Spray spray, String userId) throws NotValidSprayException {
        if(!spray.isValidRequester(userId)) {
            throw new NotValidSprayException(Code.FAIL_CODE, "Not Valid");
        }
    }
}
