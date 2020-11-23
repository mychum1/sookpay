package com.mychum1.sookpay.service;

import com.mychum1.sookpay.common.RandomTokenProcessor;
import com.mychum1.sookpay.common.ValidationProcessor;
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
public class SprayService implements SprayServiceIn{

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
     * 고유한 토큰을 구한다.
     * 최대 5번 수행한다.
     * @return
     */
    private String makeUniqueToken() {
        String token = RandomTokenProcessor.makeRandomToken();

        for (int i = 0; i < 5; i++) {
            if(!sprayRepository.existsByToken(token)) {
                break;
            }
        }

        return token;
    }

    /**
     * 뿌리기 건을 등록하면서 뿌리기를 받을 건들도 등록해둔다.
     * @param requester
     * @param roomId
     * @param amountOfMoney
     * @param personnel
     * @return
     */
    @Override
    @Transactional
    public Spray postSpray(String requester, String roomId, Long amountOfMoney, Integer personnel) {
        logger.info("call postSpray() requester : {}, roomId : {}, amountOfMoney : {}, personnel : {}", requester, roomId, amountOfMoney, personnel);

        String token = makeUniqueToken();

        Spray spray = new Spray(token, requester, roomId, amountOfMoney, personnel, Instant.now().getEpochSecond());

        long money = amountOfMoney/personnel;
        long spare = amountOfMoney%personnel;
        List<Receipt> receiptList = new ArrayList<>();
        for (int i = 0; i < personnel; i++) {
            Receipt receipt = new Receipt();
            receipt.setSpray(spray);
            receipt.setRoomId(roomId);
            receipt.setToken(spray.getToken());
            receipt.setMoney(money);
            receipt.setStatus(false);
            receipt.setReceiptOrder(i);
            receipt.setRecipient("");
            receiptList.add(receipt);
        }
        receiptList.get(0).setMoney(money+spare);
        spray.setReceiptList(receiptList);
        sprayRepository.save(spray);
        return spray;
    }

    /**
     *
     * 돈받기 요청을 수행한다.
     *
     * @param token
     * @param userId
     * @param roomId
     * @return
     * @throws NotValidSprayException
     */
    @Override
    @Transactional(rollbackOn = {NotValidSprayException.class})
    public Receipt getSpray(String token, String userId, String roomId) throws NotValidSprayException {
        logger.info("call getSpray() token : {}, userId : {}, roomId : {}", token, userId, roomId);
        Spray spray = sprayRepository.findByToken(token);
        ValidationProcessor.isValidSpray(spray);
        List<Receipt> receiptList = spray.getReceiptList();
        ValidationProcessor.isSameRoom(spray, roomId); // 같은 방의 사용자만 수령할 수 있다.

        ValidationProcessor.isValidTime(spray, getDuration); // 유효한 시간 내에 요청한 것인지 확인한다.
        ValidationProcessor.isSameRequester(spray, userId); // 뿌리기를 요청한 사용자는 받을 수 없다.
        ValidationProcessor.isValidRequester(receiptList, userId); // 이미 받은 사용자인지 확인한다.

        int size = receiptList.size();
        Receipt result = null;
        for (int i = 0; i < size; i++) {
            Receipt receipt = receiptList.get(i);
            if(receiptRepository.updateReceiptentAndStatus(userId, Instant.now().getEpochSecond(), token, roomId, receipt.getReceiptId())>0) {
                result = receipt;
               break;
            }
        }
        ValidationProcessor.checkAllTaken(result);
        return result;

    }


    /**
     *
     * 돈 뿌리기 건의 현황을 조회한다.
     *
     * @param token
     * @param userId
     * @param roomId
     * @return
     * @throws NotValidSprayException
     */
    @Override
    public SprayInfo getSprayDetail(String token, String userId, String roomId) throws NotValidSprayException {
        logger.info("call getSprayDetail() token : {}, userId : {}, roomId : {}", token, userId, roomId);
        Spray spray = sprayRepository.findByToken(token);
        ValidationProcessor.isValidSpray(spray); //유효한 뿌리기 건이 있는지 확인한다.
        List<Receipt> receiptList = spray.getReceiptList();
        ValidationProcessor.isOwnerOfSpray(userId, spray); // 뿌린 사람이 조회한 것인지 확인한다.
        ValidationProcessor.isValidTime(spray, getInfoDuration); //조회 가능 기간에 조회한 것인지 확인한다.

        //반환할 데이터 가공
        return makeSprayInfo(receiptList, spray);

    }

    /**
     * 뿌리기 요청의 상세 정보를 가공한다.
     * @param receiptList
     * @param spray
     * @return
     */
    private SprayInfo makeSprayInfo(List<Receipt> receiptList, Spray spray) {
        SprayInfo sprayInfo = new SprayInfo();
        sprayInfo.setInitDate(spray.getInitDate());
        sprayInfo.setAmountOfMoney(spray.getAmountOfMondey());
        Long total=0L;
        int receiptsSize= receiptList.size();
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


}
