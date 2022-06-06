package com.wsn.powerstrip.member.service;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.POJO.DTO.UserResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * user service和organization service不同在于,
 * user service是针对某一个用户进行的操作
 * organization service是针对一批用户进行的操作
 */
public interface UserService extends UserDetailsService {

    void sendInvitationCode(String email);

    void sendVerificationCodeByMessage(String phoneNumber);

    UserResponse register(User newUser, String invitationCode);

    UserResponse login(String email, String password);

    void deleteById(String deleteUserId);

    void deleteByEmail(String email);

    Boolean updateUserPassword(String phone, String password, String verificationCode);

    Response.Page<User> findUsersByKeywordANdRole(String keyword, String role, QueryPage queryPage);

    Response.Page<User> findUsersByRole(String role, @Nullable QueryPage queryPage);

    void updateUserInfo(User updatedUser);

    Long getNumberOfUserByRole(String role);

    Boolean resetUserPassword(String userEmail);

    UserResponse findUserByEmail(String userEmail);

    void sendVerificationCode(String email);

}

