package com.wsn.powerstrip.communication.service.impl;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import com.wsn.powerstrip.communication.POJO.VO.SendResult;
import com.wsn.powerstrip.communication.service.MessageService;
import com.wsn.powerstrip.device.constants.SensorType;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wsn.powerstrip.communication.POJO.VO.SmsNet.sendMsg;

/**
 * @author hj
 * @date 2019/10/25 15:54
 * @Modifred wsw
 */
@Order(4)
@Component("MessageServiceImpl")
public class MessageServiceImpl implements MessageService {

    private String appid = "prim2019223";
    private String appkey = "ABCabc@123";
    private int templateId = 454981;
    private String smsSign = "易书桥";

    /**
     * 发送验证码
     * @param content 验证码的内容，注意不能是字符，只能是数字
     * @param mobiles 手机号
     * @return
     */
    public String sendVerificationCode(String content, String mobiles){
        String result = null;
        Pattern p = Pattern.compile("[0-9]{4,8}");
        Matcher m = p.matcher(content);
        if (m.find()) {
            try {
                result = sendMsg(appid, appkey, mobiles, "邀请码："+content, "0");
                if (!result.contains("success")) throw new Exception();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 发送预警信息
     * 格式如下
     * @param params {"postion","x","y","xx"} 比如 {"一期1号","1","2","温度"}
     * @param mobiles {"xxxxxxx"};
     */
    public String sendPoliticsWarnSms(String[] params, String mobiles) {
        String result = null;
        try {
            result = sendMsg(appid, appkey, mobiles,
                    "预警提醒：一期" + params[0]+ "号大屏坐标("+params[1]+","+params[2]+") ,其温度为:"+params[3]+"℃ 超过预警值,请及时处理！"
                    , "0");
            if (!result.contains("success")) throw new Exception();
        }catch (Exception e){
            e.printStackTrace();
        }
        /*JSONObject responseBody = JSONObject.fromObject(result);
        if(responseBody.getString("sid") == null) {
            return null;
        }
        SendResult sendResult = new SendResult(responseBody.getString("sid"), responseBody.getString("errmsg"), "Tencent");
        if (sendResult.getRspcod().equals("OK")) {
            sendResult.setRspcod("success");
        }*/
        return result;
    }
}
