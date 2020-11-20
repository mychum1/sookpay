package com.mychum1.sookpay.controller;

import com.mychum1.sookpay.common.Code;
import com.mychum1.sookpay.domain.Receipt;
import com.mychum1.sookpay.domain.Response;
import com.mychum1.sookpay.domain.Spray;
import com.mychum1.sookpay.domain.SprayInfo;
import com.mychum1.sookpay.exception.NotValidSprayException;
import com.mychum1.sookpay.service.SprayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SprayService sprayService;



    /**
     * 돈뿌리기 요청건을 생성한다.
     * @param personnel : 뿌릴 인원
     * @param money : 뿌릴 금액
     * @param roomId : 뿌릴 방 번호
     * @param userId : 뿌린 사람
     * @return Response
     */
    @PostMapping("/spray")
    public ResponseEntity<Response> postSpray(@RequestParam(value = "personnel", required=true) Integer personnel,
                                              @RequestParam(value = "money", required = true) Long money,
                                              @RequestHeader(value = "X-USER-ID", required = true) String userId,
                                              @RequestHeader(value = "X-ROOM-ID", required = true) String roomId) {
        logger.info("call postSpray() personnel:{}, money:{}, X-USER-ID:{}, X-ROOM-ID:{}", personnel, money, userId, roomId);

        if(personnel<=0) {
            return new ResponseEntity<>(new Response<>(Code.FAIL_CODE, Code.FAIL_MSG,null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try{
            Spray spray = sprayService.postSpray(userId, roomId, money, personnel);
            return new ResponseEntity<>(new Response<>(Code.SUCCESS_CODE, Code.SUCCESS_MSG,spray), HttpStatus.OK);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Response<>(Code.FAIL_CODE, e.getMessage(),null), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @GetMapping("/spray")
    public ResponseEntity<Response> getSpray(@RequestParam(value = "token", required=true) String token,
                                              @RequestHeader(value = "X-USER-ID", required = true) String userId,
                                              @RequestHeader(value = "X-ROOM-ID", required = true) String roomId) {
        logger.info("call getSpray() token:{}, X-USER-ID:{}, X-ROOM-ID:{}", token, userId, roomId);
        //토큰은 3자리기때문에 중복될 수 있다.
        //여러 서버, 인스턴스에서 동시에 발급요청을 한다면 문제가 생길 수 있다.
        //  일단 insert를 한 후 token으로 조회한 receipt들 중에서 내 이전에 받은 사람들이 총 인원수를 넘어간다면 예외 발생시켜서 롤백.
        //  이렇게 되면 총 요청수가
        //      조회(받을 자격 되는지. 단, 조회할 때 receipt도 다 가져온다. 이미 저장된 받은 사람들 체크.. 하려고 하니까 무조건 Insert하는 시점에 맞물리면 롤백되겠구나.),
        //      저장,
        //      조회(다시 조회해서 받을 수있었는지 확인)해서 3번이 필수로 됨..(이때 status를 true로 바꿔준다. 유효하다면)

        try {
            Receipt result = sprayService.getSpray(token, userId, roomId);
            if(result == null) {
                throw new NotValidSprayException(Code.FAIL_CODE, Code.FAIL_MSG);
            }
            return new ResponseEntity<>(new Response<>(Code.SUCCESS_CODE, Code.SUCCESS_MSG, null), HttpStatus.OK);
        }catch(NotValidSprayException e) {
            return new ResponseEntity<>(new Response<>(Code.FAIL_CODE, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }

    @GetMapping("/spray/info")
    public ResponseEntity<Response> getSprayInfo(@RequestParam(value = "token", required=true) String token,
                                             @RequestHeader(value = "X-USER-ID", required = true) String userId,
                                             @RequestHeader(value = "X-ROOM-ID", required = true) String roomId) {
        logger.info("call getSprayInfo() token:{}, userId:{},roomId:{}", token, userId, roomId);

        try {
            SprayInfo sprayInfo = sprayService.getSprayDetail(token, userId, roomId);
            return new ResponseEntity<>(new Response<>(Code.SUCCESS_CODE, Code.SUCCESS_MSG, sprayInfo), HttpStatus.OK);

        }catch (NotValidSprayException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Response<>(Code.FAIL_CODE, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
