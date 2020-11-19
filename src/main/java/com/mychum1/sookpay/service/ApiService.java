package com.mychum1.sookpay.service;

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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SprayRepository sprayRepository;

    @Autowired
    ReceiptRepository receiptRepository;

    public SprayInfo getSprayDetail(String token, String userId, String roomId) throws NotValidSprayException {
        List<Receipt> receiptList = receiptRepository.findByToken(token);
        //TODO 양방향으로 mapping 해주어야 할까?
        Spray spray;

        if(receiptList.size()==0) {
            spray = sprayRepository.findByTokenAndRoomIdAndRequester(token, roomId, userId);
            //TODO 구조 더 가독성 좋게 변경할 것
            //뿌린 사람만 조회할 수 있다.
            if (spray == null) {
                throw new NotValidSprayException(500,"No Requester");
            }
        }else {

            receiptList.forEach(it-> System.out.println(it.toString()));
            spray = receiptList.get(0).getSpray();
        }
        //뿌린시각, 뿌린 금액, 받기완료된 금액, 받기 완료된 정보[받은 금액, 받은 사람,]


        //7일간만 조회 가능
        if(!spray.isValidTime((60L*60*24*7))) {
           throw new NotValidSprayException(500,"Time Over");
        }

        SprayInfo sprayInfo = new SprayInfo();
        sprayInfo.setInitDate(spray.getInitDate());
        sprayInfo.setAmountOfMoney(spray.getAmountOfMondey());
        Long total=0L;
        int receiptsSize=receiptList.size();
        List<SprayInfo.ReceivedInfo> receivedInfos = new ArrayList<SprayInfo.ReceivedInfo>();
        for (int i = 0; i < receiptsSize; i++) {
            Receipt receipt = receiptList.get(i);
            Long money = receipt.getMoney();
            total += money;

            receivedInfos.add(new SprayInfo.ReceivedInfo(receipt.getRecipient(), money));

        }
        sprayInfo.setTotalReceived(total);
        sprayInfo.setReceivedInfoList(receivedInfos);

        return sprayInfo;

    }

    public Spray postSpray(String requester, String roomId, Long amountOfMoney, Integer personnel) {
        //token 3자리 암호화
        Spray spray = new Spray(RandomTokenProcessor.makeRandomToken(), requester, roomId, amountOfMoney, personnel, Instant.now().getEpochSecond());
        return sprayRepository.save(spray);
    }

    @Transactional(rollbackOn = {NotValidSprayException.class})
    public void getSpray(String token, String userId, String roomId) throws NotValidSprayException {
        Spray spray = sprayRepository.findByTokenAndRoomId(token, roomId);

        //조회할게 없음.
        if(spray == null) {
            throw new NotValidSprayException(500, "No Valid Spray");
        }
        //1. 한 사용자는 한 번만
        //2. 받는 금액을 응답값으로
        //3. 동일한 대화방에 속한 사용자만 받을 수 있다.
        //4. 10분간만 유효
        //5. 본인은 받지 못함
        //TODO 10분은 설정으로
        Long now = Instant.now().getEpochSecond();
        if(isValidSpray(spray, (60*10L), userId)) {

            Receipt receipt = new Receipt();
            receipt.setStatus(false);
            receipt.setSpray(spray);
            receipt.setToken(token);
            receipt.setRecipient(userId);
            receipt.setRoomId(roomId);
            receipt.setMoney(spray.getAmountOfMondey()/spray.getPersonnel()); //TODO 미리 저장할까? + 10진수 , 1000원 나누기 3하면 333,333,333 이면 1원은?
            receipt.setInitDate(now); //TODO set함수에서 체크하는걸 넣는건 어떨까?
            Receipt savedReceipt = receiptRepository.save(receipt);
            List<Receipt> receiptList=receiptRepository.findByTokenAndRoomIdAndInitDateLessThan(token, roomId, now);
            boolean takeBefore = false;
            int size = receiptList.size();

            for (int i = 0; i < size; i++) {

                if(receiptList.get(i).getRecipient().equals(userId)) {
                    takeBefore=true;
                    break;
                }
            }

            if(receiptList.size() >= spray.getPersonnel() || takeBefore) {
                throw new NotValidSprayException(500,"Already taken");
            }

            //업데이트
            receiptRepository.updateStatusById(true, savedReceipt.getReceiptId());

        }else {
            throw new NotValidSprayException(500,"Not Valid");
        }

    }


    public boolean isValidSpray(Spray spray, Long duration, String requester) {
        return spray.isValidRequester(requester) && spray.isValidTime(duration);
    }


}
