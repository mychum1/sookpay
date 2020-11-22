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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
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
            return new ResponseEntity<>(new Response<>(Code.SUCCESS_CODE, Code.SUCCESS_MSG,spray.getToken()), HttpStatus.OK);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new Response<>(Code.FAIL_CODE, e.getMessage(),null), HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    /**
     * 돈 받기 요청을 처리한다.
     *
     * @param token
     * @param userId
     * @param roomId
     * @return
     */
    @GetMapping("/spray")
    public ResponseEntity<Response> getSpray(@RequestParam(value = "token", required=true) String token,
                                              @RequestHeader(value = "X-USER-ID", required = true) String userId,
                                              @RequestHeader(value = "X-ROOM-ID", required = true) String roomId) {
        logger.info("call getSpray() token:{}, X-USER-ID:{}, X-ROOM-ID:{}", token, userId, roomId);

        try {
            Receipt result = sprayService.getSpray(token, userId, roomId);
            return new ResponseEntity<>(new Response<>(Code.SUCCESS_CODE, Code.SUCCESS_MSG, result.getMoney()), HttpStatus.OK);
        }catch(NotValidSprayException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new Response<>(e.getCode(), e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * 뿌리기 건의 현황을 조회한다.
     * @param token
     * @param userId
     * @param roomId
     * @return
     */
    @GetMapping("/spray/info")
    public ResponseEntity<Response> getSprayInfo(@RequestParam(value = "token", required=true) String token,
                                             @RequestHeader(value = "X-USER-ID", required = true) String userId,
                                             @RequestHeader(value = "X-ROOM-ID", required = true) String roomId) {
        logger.info("call getSprayInfo() token:{}, userId:{},roomId:{}", token, userId, roomId);

        try {
            SprayInfo sprayInfo = sprayService.getSprayDetail(token, userId, roomId);
            return new ResponseEntity<>(new Response<>(Code.SUCCESS_CODE, Code.SUCCESS_MSG, sprayInfo), HttpStatus.OK);

        }catch (NotValidSprayException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new Response<>(Code.FAIL_CODE, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
