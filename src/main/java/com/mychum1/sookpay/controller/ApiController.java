package com.mychum1.sookpay.controller;

import com.mychum1.sookpay.domain.Response;
import com.mychum1.sookpay.exception.NotValidSprayException;
import com.mychum1.sookpay.service.ApiService;
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
    private ApiService apiService;

    @PostMapping("/spray")
    public ResponseEntity<Response> postSpray(@RequestParam(value = "personnel", required=true) Integer personnel,
                                              @RequestParam(value = "money", required = true) Long money,
                                              @RequestHeader(value = "X-USER-ID", required = true) String userId,
                                              @RequestHeader(value = "X-ROOM-ID", required = true) String roomId) {
        logger.info("call postSpray() personnel:{}, money:{}, X-USER-ID:{}, X-ROOM-ID:{}", personnel, money, userId, roomId);

        return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Success",apiService.postSpray(userId, roomId, money, personnel)), HttpStatus.OK);

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
        //      조회(다시 조회해서 받을 수있었는지 확인)해서 3번이 필수로 됨..
        try {
            apiService.getSpray(token, userId, roomId);
            return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Success", null), HttpStatus.OK);

        }catch (NotValidSprayException e) {
            return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), e.getMessage(), null), HttpStatus.OK);
        }
    }

    @GetMapping("/spray/info")
    public ResponseEntity<Response> getSprayInfo(@RequestParam(value = "token", required=true) String token,
                                             @RequestHeader(value = "X-USER-ID", required = true) String userId,
                                             @RequestHeader(value = "X-ROOM-ID", required = true) String roomId) {
        logger.info("call getSprayInfo() personnel:{}, money:{}, X-USER-ID:{}, X-ROOM-ID:{}", token, userId, roomId);

        return null;
    }
}
