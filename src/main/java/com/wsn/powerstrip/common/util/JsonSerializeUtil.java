package com.wsn.powerstrip.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @Author: wangzilinn@gmail.com
 * @Date: 4/11/2021 7:33 PM
 */
public class JsonSerializeUtil {

    public static String mapToJson(Map<?, ?> map) {
        // 这里没有使用静态的ObjectMapper,因为网上说可能有死锁的风险
        // https://stackoverflow.com/questions/3907929/should-i-declare-jacksons-objectmapper-as-a-static-field
        ObjectMapper mapper = new ObjectMapper();
        String result = "";
        try {
            result =  mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            System.out.println("map = " + map);
            e.printStackTrace();
        }
        return result;
    }
}
