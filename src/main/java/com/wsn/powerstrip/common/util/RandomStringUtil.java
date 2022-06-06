package com.wsn.powerstrip.common.util;

public class RandomStringUtil {

    //生成随机数
    private static int getRandom(int count) {
        return (int) Math.round(Math.random() * (count));
    }


    //生成length位随机字符串
    public static String getRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        String string = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = string.length();
        for (int i = 0; i < length; i++) {
            sb.append(string.charAt(getRandom(len - 1)));
        }
        return sb.toString();
    }

    public static String getRandomNumber(int length){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(getRandom(9));
        }
        return sb.toString();
    }

}
