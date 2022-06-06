package com.wsn.powerstrip.member.DAO;

import com.mongodb.DuplicateKeyException;
import com.wsn.powerstrip.member.POJO.DO.OperationRecord;
import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: xianyi_zhou@163.com
 * @Description:
 * @Date: Created in 3:32 PM 24/11/2021
 */
@Repository
@Slf4j
public class OperationRecordDAO {

    @Resource
    private MongoTemplate mongoTemplateForRecord;
    final private String RECORD_COLLECTION = "record";

    public OperationRecord addRecord(OperationRecord operationRecord) {
        try {
            return mongoTemplateForRecord.insert(operationRecord, RECORD_COLLECTION);
        } catch (DuplicateKeyException e) {
            throw new UserException(400, "新建记录失败, 主键已存在");
        }
    }


    public OperationRecord findRecordByEmail(String userEmail) {
        Query query = new Query(Criteria.where("email").is(userEmail));
        return mongoTemplateForRecord.findOne(query, OperationRecord.class, RECORD_COLLECTION);
    }

    public OperationRecord findRecordByNickname(String nickname) {
        Query query = new Query(Criteria.where("nickname").is(nickname));
        return mongoTemplateForRecord.findOne(query, OperationRecord.class, RECORD_COLLECTION);
    }


    public List<OperationRecord> findRecordsByRole(String role,@Nullable QueryPage queryPage) {
        Query query = new Query(Criteria.where("role").is(role));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForRecord.find(query, OperationRecord.class, RECORD_COLLECTION);
    }


    public boolean deleteRecordByEmail(String userEmail){
        return mongoTemplateForRecord.remove(new Query(Criteria.where("email").is(userEmail)), OperationRecord.class, RECORD_COLLECTION).wasAcknowledged();
    }

    public List<OperationRecord> findRecordsByKeyword(String keyword, @Nullable QueryPage queryPage) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("nickname").regex(keyword));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForRecord.find(query, OperationRecord.class, RECORD_COLLECTION);
    }

    public List<OperationRecord> findRecordsByKeywordAndRole(String keyword, String role, @Nullable QueryPage queryPage) {
        keyword = String.format("^.*%s.*$", keyword);//^匹配开头, $匹配结尾, 整体含义为中间包含指定值
        Query query = new Query(Criteria.where("role").is(role).and("nickname").regex(keyword));
        if (queryPage != null) {
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        }
        return mongoTemplateForRecord.find(query, OperationRecord.class, RECORD_COLLECTION);
    }

    public List<OperationRecord> findRecordsByPage(QueryPage queryPage) {
            Query query = new Query();
            PageRequest pageRequest = PageRequest.of(queryPage.getPageForMongoDB(), queryPage.getLimit());
            query = query.with(pageRequest);
        return mongoTemplateForRecord.find(query, OperationRecord.class, RECORD_COLLECTION);
    }

    public List<OperationRecord> findRecords() {
        return mongoTemplateForRecord.findAll(OperationRecord.class,"record");
    }
}
