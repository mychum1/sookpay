package com.mychum1.sookpay.service;

import com.mychum1.sookpay.domain.Receipt;
import com.mychum1.sookpay.domain.Spray;
import com.mychum1.sookpay.domain.SprayInfo;
import com.mychum1.sookpay.exception.NotValidSprayException;

public interface SprayServiceIn {

    Spray postSpray(String requester, String roomId, Long amountOfMoney, Integer personnel) throws NotValidSprayException;

    Receipt getSpray(String token, String userId, String roomId) throws NotValidSprayException;

    SprayInfo getSprayDetail(String token, String userId, String roomId) throws NotValidSprayException ;
}
