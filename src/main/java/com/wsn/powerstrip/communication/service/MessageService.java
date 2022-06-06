package com.wsn.powerstrip.communication.service;

import com.github.qcloudsms.httpclient.HTTPException;
import com.wsn.powerstrip.communication.POJO.VO.SendResult;

public interface MessageService {

    String sendVerificationCode(String content, String mobiles) throws HTTPException;

    String sendPoliticsWarnSms(String[] params, String mobiles);
}
