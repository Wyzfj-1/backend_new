package com.wsn.powerstrip.member.service.impl;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.member.DAO.OperationRecordDAO;
import com.wsn.powerstrip.member.POJO.DO.OperationRecord;
import com.wsn.powerstrip.member.service.OperationRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class OperationRecordServiceImpl implements OperationRecordService {
    private final OperationRecordDAO operationRecordDAO;

    @Override
    public OperationRecord addRecord(OperationRecord record) {
        return operationRecordDAO.addRecord(record);
    }

    @Override
    public OperationRecord findRecordByEmail(String email) {
        return operationRecordDAO.findRecordByEmail(email);
    }

    @Override
    public OperationRecord findRecordByNickname(String nickname) {
        return operationRecordDAO.findRecordByNickname(nickname);
    }

    @Override
    public List<OperationRecord> findRecordsByRole(String role,QueryPage page) {
        return operationRecordDAO.findRecordsByRole(role,page);
    }

    @Override
    public boolean deleteRecordByEmail(String email) {
        return operationRecordDAO.deleteRecordByEmail(email);
    }

    @Override
    public List<OperationRecord> getRecords(QueryPage page) {
        if (page == null){
            return operationRecordDAO.findRecords();
        }
        return operationRecordDAO.findRecordsByPage(page);
    }
}
