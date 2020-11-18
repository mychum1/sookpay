package com.mychum1.sookpay.service;

import com.mychum1.sookpay.domain.Receipt;
import com.mychum1.sookpay.domain.Spray;
import com.mychum1.sookpay.exception.NotValidSprayException;
import com.mychum1.sookpay.repository.ReceiptRepository;
import com.mychum1.sookpay.repository.SprayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class ApiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MoneyProcessor moneyProcessor;

    @Autowired
    SprayRepository sprayRepository;

    @Autowired
    ReceiptRepository receiptRepository;

    public Spray postSpray(String requester, String roomId, Long amountOfMoney, Integer personnel) {
        //token 3자리 암호화
        Spray spray = new Spray("tok", requester, roomId, amountOfMoney, personnel, Instant.now().getEpochSecond());
        return sprayRepository.save(spray);
    }

    @Transactional(rollbackOn = {NotValidSprayException.class})
    public void getSpray(String token, String userId, String roomId) throws NotValidSprayException {
        Spray spray = sprayRepository.findByTokenAndRoomId(token, roomId);

        //1. 한 사용자는 한 번만
        //2. 받는 금액을 응답값으로
        //3. 동일한 대화방에 속한 사용자만 받을 수 있다.
        //4. 10분간만 유효
        //5. 본인은 받지 못함
        //TODO 10분은 설정으로
        Long now = Instant.now().getEpochSecond();
        if(isValidSpray(spray, (60*10L), userId)) {

            Receipt receipt = new Receipt();
            receipt.setSpray(spray);
            receipt.setToken(token);
            receipt.setRecipient(userId);
            receipt.setRoomId(roomId);
            receipt.setMoney(spray.getAmountOfMondey()/spray.getPersonnel()); //TODO 미리 저장할까? + 10진수
            receipt.setInitDate(now); //TODO set함수에서 체크하는걸 넣는건 어떨까?
            receiptRepository.save(receipt);
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

        }else {
            throw new NotValidSprayException(500,"Not Valid");
        }

    }



    public boolean isValidSpray(Spray spray, Long duration, String requester) {
        return spray.isValidRequester(requester) && spray.isValidTime(duration);
    }

    public void test() {
        //TODO token 3자리 암호화 + 중복일수도 있다!
        Spray spray = new Spray();
        spray.setToken("tes");
        spray.setRequester("requestor");
        spray.setRoomId("roomid");
        spray.setAmountOfMondey(5000L);
        spray.setPersonnel(3);
        spray.setInitDate(Instant.now().getEpochSecond());
        sprayRepository.save(spray);


        Receipt r = new Receipt();
        r.setSpray(spray);
        receiptRepository.save(r);

        receiptRepository.findAll().forEach(it -> System.out.println(it.toString()));
        System.out.println("========");
        sprayRepository.findAll().forEach(it->System.out.println(it.toString()));

        //moneyProcessor.test();
    }

}
