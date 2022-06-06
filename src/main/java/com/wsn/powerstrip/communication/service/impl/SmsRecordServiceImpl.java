package com.wsn.powerstrip.communication.service.impl;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.communication.DAO.SmsRecordDAO;
import com.wsn.powerstrip.communication.POJO.DTO.SmsRecord;
import com.wsn.powerstrip.communication.service.SmsRecordService;
import com.wsn.powerstrip.member.POJO.DO.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class SmsRecordServiceImpl implements SmsRecordService {
    final private SmsRecordDAO smsRecordDAO;

    @Override
    public SmsRecord addSmsRecord(SmsRecord smsRecord) {
        if (!Objects.isNull(smsRecord))
            return smsRecordDAO.addSmsRecord(smsRecord);
        return null;
    }

    @Override
    public Response.Page<SmsRecord> getSmsRecords(QueryPage queryPage) {
        List<SmsRecord> result = smsRecordDAO.getSmsRecords(queryPage);
        Long total = smsRecordDAO.getNumberOfSmsRecords();
        return new Response.Page<>(result, queryPage, total);
    }
}
