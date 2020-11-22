package com.mychum1.sookpay.common;

import java.util.Random;

public class RandomTokenProcessor {

    static String[] key = new String[]{"a","b","c","d","e","f",
            "g","h","i","j","k","l",
            "m","n","o","p","q","r",
            "s","t","u","v","w","x","y","z",
            "A","B","C","D","E","F",
            "G","H","I","J","K","L",
            "M","N","O","P","Q","R",
            "S","T","U","V","W","X","Y","Z",
            "1","2","3","4","5","6","7","8","9"};

    static Random random = new Random();
    public static String makeRandomToken() {

        StringBuffer sb = new StringBuffer(key[random.nextInt(key.length)]);
        for (int i = 0; i < 2; i++) {
            sb.append(key[random.nextInt(key.length)]);
        }
        return sb.toString();
    }

}
