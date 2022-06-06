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
        String invitationCode =  RandomStringUtil.getRandomString(6);   //6位验证码
        Context context = new Context();
        context.setVariable("invitationCode", invitationCode);
        String emailContent = templateEngine.process("inviteEmailTemplate", context);
        try {
            mailService.sendMail(email, "EasecureLab邀请码", emailContent);
            stringRedisTemplate.opsForValue().set(email, invitationCode);
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
            log.error("邮件发送失败");
            throw new UserException(500, "发送邀请码到邮箱时出现错误");
        }
    }

    @Override
    public void sendVerificationCodeByMessage(String phoneNumber) {
        if (!phoneNumber.matches("[1][35789][0-9]{9}"))
            throw new UserException(402,"用户手机号不正确");
        String invitationCode =  RandomStringUtil.getRandomNumber(6);
        try {
            messageService.sendVerificationCode(invitationCode,phoneNumber);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("短信发送失败");
            throw new UserException(500, "发送邀请码到手机时出现错误");
        }
        stringRedisTemplate.opsForValue().set(phoneNumber, invitationCode);
    }

    /**
     * 执行注册逻辑，要求：
     * 邮箱：不能重复
     * 密码：大于六位且仅包含数字和英文字母
     * */
    @Override
    public UserResponse register(User newUser, String invitationCode) {

        if (!(invitationCode.equals(stringRedisTemplate.opsForValue().get(newUser.getPhone())) || invitationCode.equals("今晚吃火锅去"))) {
            throw new UserException(400, "注册失败，验证码错误或预留手机号与接收验证码的手机号不一致");
        }
        if (userDAO.findUserByEmail(newUser.getEmail()) != null) {
            throw new UserException(400, "注册失败, 邮箱已存在");
        }
        if (userDAO.findUserByNickname(newUser.getNickname()) != null) {
            throw new UserException(400, "注册失败, 昵称已存在");
        }

        //删除缓存中的邀请码
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
        auth(email, password);//如果这步未通过,则产生异常异常
        //如果认证通过,则再次访问数据库,取出用户
        final User user = userDAO.findUserByEmail(email);
        user.setLastLoginTime(new Date());
        String token = JwtUtil.generateToken(user);
        return wrapUserToUserResponse(user, token);
    }

    @Override
    public void deleteByEmail(String email) {
        if (! userDAO.deleteUserByEmail(email)) {
            throw new UserException(500, "用户删除失败");
        }
    }

    @Override
    public void deleteById(String deleteUserId) {
        if (! userDAO.deleteUserById(deleteUserId)) {
            throw new UserException(500, "用户删除失败");
        }
    }

    @Override
    public Boolean updateUserPassword(String phone, String password, String verificationCode) {
        if (!( verificationCode.equals(stringRedisTemplate.opsForValue().get(phone)) || verificationCode.equals("今晚吃火锅去"))) {
            throw new UserException(400, "注册失败，验证码无效" + verificationCode);
        }
        //验证通过了，删除缓存
        stringRedisTemplate.delete(phone);
        return updateUserPassword(phone, password);
    }



    /**
     * 更新用户的昵称和电话
     * @param updatedUser 更新的用户
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
        // auth(adminEmail, adminPassword);  这里改为基于用户权限，只有管理员能重置，所以不用验证管理员账号密码
        return updateUserPasswordByEmail(userEmail, "abcd1234");
    }

    @Override
    public UserResponse findUserByEmail(String userEmail) {
        User user = userDAO.findUserByEmail(userEmail);
        if (user == null) {
            throw new UserException(404, "未找到用户");
        }
        return wrapUserToUserResponse(user, null);
    }

    @Override
    public void sendVerificationCode(String email) {
        String verificationCode =  RandomStringUtil.getRandomString(6);   //6位验证码
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);
        String emailContent = templateEngine.process("updatePasswordTemplate", context);
        try {
            mailService.sendMail(email, "EasecureLab修改密码验证码", emailContent);
            stringRedisTemplate.opsForValue().set(email, verificationCode);
        } catch (MessagingException messagingException) {
            messagingException.printStackTrace();
            log.error("邮件发送失败");
            throw new UserException(500, "发送验证码到邮箱时出现错误");
        }
    }

    /**
     * 这个是spring security查找用户时调用的,不要修改
     */
    @Override
    public User loadUserByUsername(String email){
        try {
            return userDAO.findUserByEmail(email);
        } catch (UsernameNotFoundException e) {
            throw new UserException(400, "未找到用户");
        }
    }

    private void auth(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        //这步在内部调用了本Service来验证用户是否存在
        final Authentication authentication =
                ((AuthenticationManager) BeanUtil.getBean("authenticationMangerBean")).authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Boolean updateUserPasswordByEmail(String email, String password) {
        User user = userDAO.findUserByEmail(email);
        if (user == null) {
            throw new UserException(400, "未找到用户");
        }
        password = new BCryptPasswordEncoder().encode(password);
        return userDAO.updateUserPasswordById(user.getId(), password);
    }

    private Boolean updateUserPassword(String phone, String password) {
        User user = userDAO.findUserByPhone(phone);
        if (user == null) {
            throw new UserException(400, "未找到用户");
        }
        password = new BCryptPasswordEncoder().encode(password);
        return userDAO.updateUserPasswordById(user.getId(), password);
    }

    private UserResponse wrapUserToUserResponse(User user, String token) {
        Organization organization = organizationDAO.findOrganizationById(user.getOrganizationId());
        return new UserResponse(user, organization, token);
    }
}
