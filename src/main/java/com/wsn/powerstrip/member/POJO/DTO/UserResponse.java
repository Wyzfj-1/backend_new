package com.wsn.powerstrip.member.POJO.DTO;

import com.wsn.powerstrip.member.POJO.DO.Organization;
import com.wsn.powerstrip.member.POJO.DO.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 6:56 PM 08/17/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Data
@Schema(description = "返回全部的User信息,避免前端多次请求")
@NoArgsConstructor
public class UserResponse {

    private String id;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "用户Email", required = true)
    private String email;

    private String phone;
    @Schema(required = true, description = "用户所在的组织")
    @NotBlank(message = "用户所在组织id不能为空")
    private Organization organization;
    @Schema(description = "用户角色:\"admin\", \"user\", \"root\"", example = "admin", defaultValue = "user")
    private String role;
    @Schema(description = "用户访问所有api须带的Token")
    private String token;

    private Date createTime;

    private Date lastLoginTime;

    public UserResponse(User user, Organization organization, String token) {
        BeanUtils.copyProperties(user, this);
        this.token = token;
        this.organization = organization;
    }
}
