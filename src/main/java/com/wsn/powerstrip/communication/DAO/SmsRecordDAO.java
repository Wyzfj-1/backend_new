package com.wsn.powerstrip.communication.DAO;

import com.mongodb.DuplicateKeyException;
import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.communication.POJO.DTO.SmsRecord;
import com.wsn.powerstrip.member.POJO.DO.OperationRecord;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
@Slf4j
public class SmsRecordDAO {
    @Resource
    private MongoTemplate mongoTemplateForSMS;
    final private String ALARM_COLLECTION = "alarm";

    public SmsRecord addSmsRecord(SmsRecord smsRecord) {
        try {
            return mongoTemplateForSMS.insert(smsRecord, ALARM_COLLECTION);
        } catch (DuplicateKeyException e) {
            throw new UserException(400, "插入信息失败, 主键已存在");
        }
    }

    public List<SmsRecord> getSmsRecords(QueryPage queryPage) {
        PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
        Query query = new Query();
        query = query.with(pageRequest);
        return mongoTemplateForSMS.find(query, SmsRecord.class, ALARM_COLLECTION);
    }

    public Long getNumberOfSmsRecords(){
        return mongoTemplateForSMS.count(new Query(),SmsRecord.class,ALARM_COLLECTION);
    }

}
