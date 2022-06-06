package com.wsn.powerstrip.common.config.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 4:49 PM 6/18/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Component
@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("methodKey：" + methodKey);
        log.error("response.toString():" + response.toString());
        log.error("response.reason():" + response.reason());
        log.error("response.request().toString():" + response.request().toString());
        if (methodKey.contains("OceanConnectFeignClient#getHistory")) {
            // 处理从OC获得设备历史异常
            /**
            if (response.status() == 404) {
                return new OceanConnectException(500, "未找到deviceID对应的插线板，检查deviceId是否正确");
            }
            if (response.status() == 403) {
                return new OceanConnectException(403, "连接被阻断，可能是请求过快？");
            }
             */
        }
        return new Exception(response.toString());
    }

}
