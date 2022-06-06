package com.wsn.powerstrip.communication.service;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.communication.POJO.DTO.SmsRecord;
import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @Author: zxy
 * @Description: 记录短信发送记录的Service
 */
public interface SmsRecordService {
    SmsRecord addSmsRecord(SmsRecord smsRecord);

    Response.Page<SmsRecord> getSmsRecords(@Nullable QueryPage queryPage);
}
