package com.wsn.powerstrip.member.POJO.DO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Author: xianyi_zhou@163.com
 * @Description: 记录用户操作的数据，记下用户相关信息和操作的信息并存库
 * @Date: Created in 3:32 PM 24/11/2021
 */
@Data
@AllArgsConstructor
@Builder
public class OperationRecord {
    @Id
    private String id;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "用户Email")
    private String email;
    @Schema(description = "用户角色:\"admin\", \"user\"")
    private String role;
    @Schema(description = "该用户进行操作的描述")
    private String operation;
    @Schema(description = "操作的时间")
    private Date operationDate;
    @Schema(description = "用户操作的ip地址")
    private String ip;

}
