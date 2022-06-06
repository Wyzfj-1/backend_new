package com.wsn.powerstrip.member.POJO.DO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    private String id;
    @Schema(description = "用户昵称")
    private String nickname;
    @Email(message = "email格式错误")
    @NotBlank(message = "email必须不为空")
    @Schema(description = "用户Email", required = true)
    private String email;
    @NotBlank(message = "密码必须不为空")
    @Schema(required = true)
    private String password;
    @Schema(description = "用户的微信id")
    private String wechatId;
    @NotBlank(message = "phone必须不为空")
    @Schema(description = "用户预留手机号")
    private String phone;
    @Schema(required = true, description = "用户所在的组织")
    @NotBlank(message = "用户所在组织id不能为空")
    private String organizationId;
    @Schema(description = "用户角色:\"admin\", \"user\", \"root\"", example = "admin", defaultValue = "user")
    private String role;

    private Date createTime;

    private Date updateTime;

    private Date lastLoginTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (role == null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_user"));//ROLE前缀必须
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}