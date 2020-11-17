package com.mychum1.sookpay.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MoneyProcessor moneyProcessor;

    public void test() {
        moneyProcessor.test();
    }

}
