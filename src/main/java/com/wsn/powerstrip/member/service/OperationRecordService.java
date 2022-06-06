package com.wsn.powerstrip.member.service;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.member.POJO.DO.OperationRecord;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @Author: xianyi_zhou@163.com
 * @Description: 记录用户操作记录的Service
 * @Date: Created in 11:02 AM 25/11/2021
 */
public interface OperationRecordService {
    OperationRecord addRecord(OperationRecord record);

    OperationRecord findRecordByEmail(String email);

    OperationRecord findRecordByNickname(String nickname);

    List<OperationRecord> findRecordsByRole(String role, @Nullable QueryPage page);

    boolean deleteRecordByEmail(String email);

    List<OperationRecord> getRecords(@Nullable QueryPage page);
}
