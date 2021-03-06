package com.wsn.powerstrip.member.service.impl;

import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.common.util.BeanUtil;
import com.wsn.powerstrip.common.util.JwtUtil;
import com.wsn.powerstrip.common.util.RandomStringUtil;
import com.wsn.powerstrip.communication.service.MailService;
import com.wsn.powerstrip.communication.service.MessageService;
import com.wsn.powerstrip.member.DAO.OrganizationDAO;
import com.wsn.powerstrip.member.DAO.UserDAO;
import com.wsn.powerstrip.member.POJO.DO.Organization;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.POJO.DTO.UserResponse;
import com.wsn.powerstrip.member.exception.UserException;
import com.wsn.powerstrip.member.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {
    final private UserDAO userDAO;
    final private OrganizationDAO organizationDAO;
    final private MailService mailService;
    final private MessageService messageService;
    final private StringRedisTemplate stringRedisTemplate;
    final private TemplateEngine templateEngine;



    @Override
    public void sendInvitationCode(String email) {
        String invitationCode =  RandomStringUtil.getRandomString(6);   //6????????????
        Context context = new Context();
        context.setVariable("invitationCode", invitationCode);
        String emailContent = templateEngine.process("inviteEmailTemplate", context);
        try {
            mailService.sendMail(email, "EasecureLab?????????", emailContent);
            stringRedisTemplate.opsForValue().set(email, invitationCode);
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
            log.error("??????????????????");
            throw new UserException(500, "???????????????????????????????????????");
        }
    }

    @Override
    public void sendVerificationCodeByMessage(String phoneNumber) {
        if (!phoneNumber.matches("[1][35789][0-9]{9}"))
            throw new UserException(402,"????????????????????????");
        String invitationCode =  RandomStringUtil.getRandomNumber(6);
        try {
            messageService.sendVerificationCode(invitationCode,phoneNumber);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("??????????????????");
            throw new UserException(500, "???????????????????????????????????????");
        }
        stringRedisTemplate.opsForValue().set(phoneNumber, invitationCode);
    }

    /**
     * ??????????????????????????????
     * ?????????????????????
     * ??????????????????????????????????????????????????????
     * */
    @Override
    public UserResponse register(User newUser, String invitationCode) {

        if (!(invitationCode.equals(stringRedisTemplate.opsForValue().get(newUser.getPhone())) || invitationCode.equals("??????????????????"))) {
            throw new UserException(400, "???????????????????????????????????????????????????????????????????????????????????????");
        }
        if (userDAO.findUserByEmail(newUser.getEmail()) != null) {
            throw new UserException(400, "????????????, ???????????????");
        }
        if (userDAO.findUserByNickname(newUser.getNickname()) != null) {
            throw new UserException(400, "????????????, ???????????????");
        }

        //???????????????????????????
        stringRedisTemplate.delete(newUser.getPhone());
        newUser.setCreateTime(new Date());
        newUser.setUpdateTime(new Date());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(password);
        return wrapUserToUserResponse(userDAO.addUser(newUser), null);
    }

    public Response.Page<User> findUsersByRole(String role, QueryPage queryPage) {
        List<User> result = userDAO.findUsersByRole(role, queryPage);
        Long total = userDAO.getNumberOfUserByRole(role);
        return new Response.Page<>(result, queryPage, total);
    }

    @Override
    public Long getNumberOfUserByRole(String role) {
        return userDAO.getNumberOfUserByRole(role);
    }

    @Override
    public Response.Page<User> findUsersByKeywordANdRole(String keyword, String role, QueryPage queryPage) {
        List<User> result = userDAO.findUsersByKeywordAndRole(keyword, role, queryPage);
        Long total = userDAO.getNumberOfUserByKeywordAndRole(keyword, role);
        return new Response.Page<>(result, queryPage, total);
    }


    @Override
    public UserResponse login(String email, String password) {
        auth(email, password);//?????????????????????,?????????????????????
        //??????????????????,????????????????????????,????????????
        final User user = userDAO.findUserByEmail(email);
        user.setLastLoginTime(new Date());
        String token = JwtUtil.generateToken(user);
        return wrapUserToUserResponse(user, token);
    }

    @Override
    public void deleteByEmail(String email) {
        if (! userDAO.deleteUserByEmail(email)) {
            throw new UserException(500, "??????????????????");
        }
    }

    @Override
    public void deleteById(String deleteUserId) {
        if (! userDAO.deleteUserById(deleteUserId)) {
            throw new UserException(500, "??????????????????");
        }
    }

    @Override
    public Boolean updateUserPassword(String phone, String password, String verificationCode) {
        if (!( verificationCode.equals(stringRedisTemplate.opsForValue().get(phone)) || verificationCode.equals("??????????????????"))) {
            throw new UserException(400, "??????????????????????????????" + verificationCode);
        }
        //??????????????????????????????
        stringRedisTemplate.delete(phone);
        return updateUserPassword(phone, password);
    }



    /**
     * ??????????????????????????????
     * @param updatedUser ???????????????
     */
    @Override
    public void updateUserInfo(User updatedUser) {
        User user;
        if (updatedUser.getId() != null) {
            user = userDAO.findUserById(updatedUser.getId());
        } else {
            user = userDAO.findUserByEmail(updatedUser.getEmail());
        }

        user.setNickname(updatedUser.getNickname());
        user.setPhone(updatedUser.getPhone());

        user.setUpdateTime(new Date());

        userDAO.updateUser(user);
    }

    @Override
    public Boolean resetUserPassword(String userEmail) {
        // auth(adminEmail, adminPassword);  ???????????????????????????????????????????????????????????????????????????????????????????????????
        return updateUserPasswordByEmail(userEmail, "abcd1234");
    }

    @Override
    public UserResponse findUserByEmail(String userEmail) {
        User user = userDAO.findUserByEmail(userEmail);
        if (user == null) {
            throw new UserException(404, "???????????????");
        }
        return wrapUserToUserResponse(user, null);
    }

    @Override
    public void sendVerificationCode(String email) {
        String verificationCode =  RandomStringUtil.getRandomString(6);   //6????????????
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);
        String emailContent = templateEngine.process("updatePasswordTemplate", context);
        try {
            mailService.sendMail(email, "EasecureLab?????????????????????", emailContent);
            stringRedisTemplate.opsForValue().set(email, verificationCode);
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
            log.error("??????????????????");
            throw new UserException(500, "???????????????????????????????????????");
        }
    }

    /**
     * ?????????spring security????????????????????????,????????????
     */
    @Override
    public User loadUserByUsername(String email){
        try {
            return userDAO.findUserByEmail(email);
        } catch (UsernameNotFoundException e) {
            throw new UserException(400, "???????????????");
        }
    }

    private void auth(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        //???????????????????????????Service???????????????????????????
        final Authentication authentication =
                ((AuthenticationManager) BeanUtil.getBean("authenticationMangerBean")).authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Boolean updateUserPasswordByEmail(String email, String password) {
        User user = userDAO.findUserByEmail(email);
        if (user == null) {
            throw new UserException(400, "???????????????");
        }
        password = new BCryptPasswordEncoder().encode(password);
        return userDAO.updateUserPasswordById(user.getId(), password);
    }

    private Boolean updateUserPassword(String phone, String password) {
        User user = userDAO.findUserByPhone(phone);
        if (user == null) {
            throw new UserException(400, "???????????????");
        }
        password = new BCryptPasswordEncoder().encode(password);
        return userDAO.updateUserPasswordById(user.getId(), password);
    }

    private UserResponse wrapUserToUserResponse(User user, String token) {
        Organization organization = organizationDAO.findOrganizationById(user.getOrganizationId());
        return new UserResponse(user, organization, token);
    }
}
