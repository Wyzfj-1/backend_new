package com.wsn.powerstrip.member.POJO.DTO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 4:45 PM 6/24/2020
 * @Modified By:wangzilinn@gmail.com
 */
@Data
public class LoginRequest {
    @Email(message = "email格式错误")
    @NotBlank(message = "email必须不为空")
    private String email;
    @NotBlank(message = "密码部分不能为空")
    private String password;
}
