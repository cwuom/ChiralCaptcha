package com.cwuom.chiralcaptcha.util;

import java.util.Random;

public class RandomUtil {
    public static int randint(int MIN, int MAX){
        Random rand = new Random();
        return rand.nextInt(MAX - MIN + 1) + MIN;
    }
}
