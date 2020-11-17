package com.mychum1.sookpay.controller;

import com.mychum1.sookpay.domain.Response;
import com.mychum1.sookpay.service.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

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

        return null;
    }

    @GetMapping("/spray")
    public ResponseEntity<Response> getSpray(@RequestParam(value = "token", required=true) String token,
                                              @RequestHeader(value = "X-USER-ID", required = true) String userId,
                                              @RequestHeader(value = "X-ROOM-ID", required = true) String roomId) {
        logger.info("call getSpray() personnel:{}, money:{}, X-USER-ID:{}, X-ROOM-ID:{}", token, userId, roomId);

        return null;
    }
}
