package com.wsn.powerstrip.member.POJO.DO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 7:25 PM 07/10/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Data
public class Organization {
    @Id
    private String id;
    @Schema(description = "组织名称")
    @NotBlank(message = "组织名称不能为空")
    @Length(max = 100, message = "组织名称长度必须小于100")
    private String name;

    private Date createTime;

    private Date updateTime;

}
