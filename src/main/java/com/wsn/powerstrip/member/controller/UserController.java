package com.wsn.powerstrip.member.controller;


import com.wsn.powerstrip.common.POJO.DTO.QueryPage;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.common.annotation.WebLog;
import com.wsn.powerstrip.communication.POJO.DTO.SmsRecord;
import com.wsn.powerstrip.communication.service.SmsRecordService;
import com.wsn.powerstrip.member.DAO.UserDAO;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.POJO.DTO.LoginRequest;
import com.wsn.powerstrip.member.exception.UserException;
import com.wsn.powerstrip.member.service.OperationRecordService;
import com.wsn.powerstrip.member.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 一个Controller应该只调用一次service
 *   @Author: wangzilinn@gmail.com
 *   @Description:
 *   @Modified By:xianyi_zhou@163.com
 */


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Tag(name = "User", description = "单个用户管理接口,如登录注册等")
@Slf4j
@Validated
@CrossOrigin
public class UserController {

    final private UserService userService;
    final private OperationRecordService operationRecordService;

    @GetMapping("/invite")
    @Operation(summary = "用户获取邀请码", description = "用户输入电话,后台向用户发送邀请码")
    @WebLog
    public Response<?> getInvitationCode(@Parameter(description = "用户手机号")
                                             @RequestParam("phoneNumber") String phoneNumber) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("wsn405407");
        System.out.println(password);
        userService.sendVerificationCodeByMessage(phoneNumber);
        return new Response<>("已向用户发送邀请码");
    }

   /* @GetMapping("/invite")  邮箱验证方式
    @Operation(summary = "用户获取邀请码", description = "用户输入邮箱,后台向用户发送邀请码")
    @WebLog
    public Response<?> getInvitationCode(@Parameter(description = "用户邮箱")
                                         @Email(message = "邮箱格式校验错误") @RequestParam("email") String email) {
        userService.sendInvitationCode(email);
        return new Response<>("已向用户发送邀请码");
    }*/

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "须向/api/user/invite请求邀请码,否则也可用默认邀请码\"今晚吃火锅去\"<br/>" +
            "注意："+"1、 只有email、password、phone、organizationId字段是必要的; username就是email;   " +
            "2、  nickname和email必须未被注册 " + "3、  用户输入手机号必须和接收验证码的手机号一致"+
            "4、  必须删除id字段,详情见Schema")
    //@WebLog //这里不能用weblog,否则参数1会导致循环引用造成OOM
    public Response<?> register(@Parameter(description = "json类型的用户实体") @Valid @NotNull @RequestBody User user,
                                @Parameter(description = "用户邀请码，从短信中获得") @Length(max = 6, min = 6, message = "邀请码长度为6")
                                @RequestParam("invitationCode") String invitationCode) {
        //检查输入对象必要参数是否足够:
        //必须为空的部分:
        if (user.getId() != null) {
            throw new UserException(400, "User实体不应该有Id, 应为数据库分配" + user.toString());
        }
        //User中不为空的字段用注解去校验了
        if (passwordIsTooWeak(user.getPassword())) {
            throw new UserException(402, "密码长度必须大于6，并且必须且只包含数字和英文字母");
        }
        //执行注册逻辑
        if (userService.register(user, invitationCode) != null)
            return new Response<>(user);
        else
            throw new UserException(400, "用户注册失败");
    }

    //@CrossOrigin没有logout操作？
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名:zhouxianyi@qq.com, 密码:zxy2333")
    @WebLog
    public Response<?> SignIn(@Parameter(description = "用户名和密码", required = true)
                              @Valid @NotNull @RequestBody LoginRequest loginInfo
    ) throws AuthenticationException {
        String email = loginInfo.getEmail();
        String password = loginInfo.getPassword();
        return new Response<>(userService.login(email, password));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户", description = "该请求需要header中加入token,并且要是管理员权限")
    @WebLog(description = "删除了一个用户")
    public Response<?> delete(@Parameter(description = "需要删除的用户邮箱")
                              @Email(message = "邮箱格式校验错误") @RequestParam("email") String email){
        userService.deleteByEmail(email);
        return new Response<>("删除成功");
    }


    @GetMapping("/password")
    @Operation(summary = "发送用户修改密码确认验证码", description = "仅有一个参数,即申请修改的用户的手机号，并且该手机号必须" +
            "和预存的手机号一致，否则不能发送验证码")
    @WebLog
    public Response<?> changePasswordEmail(@Parameter(description = "想要修改密码的用户的手机号")
                                           @RequestParam(value = "phoneNumber") String phoneNumber) {
        //校验输入的手机号是否是预存的手机号
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();

        if (!phoneNumber.equals(user.getPhone()))
            throw new UserException(402,"输入手机号和预留手机号不一致");

        userService.sendVerificationCodeByMessage(phoneNumber);
        return new Response<>("短信发送成功");
    }


    @PutMapping("/password")
    @Operation(summary = "用户修改密码", description = "密码字段为password，需要获取json方式封装对象, 该请求需要header中加入token")
    @WebLog
    public Response<?> changePassword(@Parameter(description = "向用户邮箱发送的验证码,通用验证码为\"今晚吃火锅去\"")
                                      @RequestParam(value = "verificationCode") String verificationCode,
                                      @Parameter(description = "新密码")
                                      @NotNull @RequestParam(value = "password") String password,
                                      Authentication authentication) {
        //用当前认证的用户的email更新自己的密码
        User details = (User) authentication.getDetails();
        //检查密码强度:密码长度必须大于6，并且必须只包含数字和英文字母
        if (passwordIsTooWeak(password)) {
            throw new UserException(402, "密码长度必须大于6，并且必须且只包含数字和英文字母");
        }
        //调用更新密码服务
        if (userService.updateUserPassword(details.getPhone(), password, verificationCode)) {
            return new Response<>(200, "修改成功");
        } else {
            throw new UserException(403, "数据库未更新");
        }
    }

    @PutMapping("/password/reset")
    @Operation(summary = "管理员重置用户密码", description = "当前用户需要有管理员权限,重置密码为:abcd1234." +
            "注意:以下示例中url中的userEmail是希望被重置密码的用户的email")
    @WebLog(description = "重置了他人密码")
    public Response<?> resetPassword(@Parameter(description = "重置密码的用户email") @Email @RequestParam("userEmail") String userEmail){
        userService.resetUserPassword(userEmail);
        return new Response<>("重置密码成功");
    }
    /*public Response<?> resetPassword(@Parameter(description = "管理员的用户名和密码") @Valid @NotNull @RequestBody LoginRequest adminLoginRequest,
                                     @Parameter(description = "重置密码的用户email") @Email @RequestParam("userEmail") String userEmail){
        userService.resetUserPassword(adminLoginRequest.getEmail(), adminLoginRequest.getPassword(), userEmail);
        return new Response<>("重置密码成功");
    }*/
    /**
     * 下面这个请求有个bug，认证只认证当前登录用户，但是修改的
     * 时候是根据提供的email去修改信息的，也就意味着一
     * 个用户可以修改其他用户的信息,所以采用第二个请求方法
     * */
   /* @PutMapping
    @Operation(summary = "修改个人信息", description = "仅昵称和电话可以修改")
    @WebLog
    public Response<?> updateUserInfo(@Parameter(description = "用户实体") @NotNull @RequestBody User user) {
        //方法参数中没有加@Valid对用户加以验证,是因为前端可能不把所有必须字段传过来,只把修改的部分传过来了
        if (user.getId() == null && user.getEmail() == null) {
            throw new UserException(400, "参数没有用户id或email，无法识别用户");
        }
        userService.updateUserInfo(user);
        return new Response<>("更新用户信息成功");
    }*/
    @PutMapping
    @Operation(summary = "本人修改个人信息", description = "仅昵称和电话可以修改，注意：可以不用输入用户id和email，后端" +
            "会取出当前认证用户的id和email进行赋值")
    @WebLog
    //方法参数中没有加@Valid对用户加以验证,是因为前端可能不把所有必须字段传过来,只把修改的部分传过来了
    public Response<?> updateUserInfo(@Parameter(description = "用户实体")
                                      @NotNull @RequestBody User user,
                                      Authentication authentication) {
        //这里取出认证过的用户的id和email赋值给前端传过来的user，防止登录一个账号而去修改其他账号的信息
        User details = (User)authentication.getDetails();
        user.setEmail(details.getEmail());
        user.setId(details.getId());
        userService.updateUserInfo(user);
        return new Response<>("更新用户信息成功");
    }

    @PutMapping("/update")
    @Operation(summary = "管理员修改个人信息，需要管理员权限", description = "仅修改昵称和电话")
    @WebLog(description = "修改他人信息")
    //方法参数中没有加@Valid对用户加以验证,是因为前端可能不把所有必须字段传过来,只把修改的部分传过来了
    public Response<?> update(@Parameter(description = "用户实体")
                                      @NotNull @RequestBody User user) {
        userService.updateUserInfo(user);
        return new Response<>("更新用户信息成功");
    }

    @GetMapping("/userList")
    @Operation(summary = "获得对应角色的数量或列表, role是必填项, 如果justNumber=true,则只返回该角色的数量,且其他参数无用,否则查询的页数是必填项")
    @WebLog
    public Response<?> getUserList(@Parameter(description = "用户的角色，admin或者user")
                                   @RequestParam(value = "role") String role,
                                   @Parameter(description = "模糊查询用户名")
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   @Parameter(description = "查询的页数")
                                   @Range(min = 1)
                                   @RequestParam(value = "page", required = false) Integer page,
                                   @Parameter(description = "每页的长度")
                                   @Range(min = 1)
                                   @RequestParam(value = "limit", defaultValue = "10") int limit,
                                   @Parameter(description = "是否只返回用户数量")
                                   @RequestParam(value = "justNumber", required = false) Boolean justNumber) {
        if (justNumber == null || !justNumber) {
            if (page == null) {
                throw new UserException(400, "mast have page param when justNumber is not specified");
            }
            if (keyword != null) {
                return new Response<>(userService.findUsersByKeywordANdRole(keyword,
                        role, new QueryPage(page, limit)));
            } else {
                return new Response<>(userService.findUsersByRole(role, new QueryPage(page,
                        limit)));
            }
        } else {
            Long userNumberByRole = userService.getNumberOfUserByRole(role);
            return new Response<>(userNumberByRole);
        }
    }


    @GetMapping("/profile")
    @Operation(summary = "根据用户email查找用户", description = "该请求需要header中加入token")
    public Response<?> getUserByEmail(@Email @RequestParam("userEmail") String userEmail,
                                      @RequestHeader Map<String, String> head
    ) {
        log.debug("Authorization: {}", head.get("Authorization"));
        log.debug("authorization: {}", head.get("authorization"));
        return new Response<>(userService.findUserByEmail(userEmail));
    }

    @GetMapping("/records")
    @Operation(summary = "返回用户的操作记录,可以不传Page数据，如果Page数据为空，则会返回全部数据", description = "该请求需要header中加入token")
    public Response<?> getOperationRecords(@Parameter(description = "查询的页数")
                                               @Range(min = 1)
                                               @RequestParam(value = "page", required = false) Integer page,
                                           @Parameter(description = "每页的长度")
                                               @Range(min = 1)
                                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit){
        if (page == null || limit == null)
            return new Response<>(operationRecordService.getRecords(null));
        return new Response<>(operationRecordService.getRecords(new QueryPage(page,limit)));
    }



    /**
     * 密码长度必须大于6，并且必须且只包含数字和英文字母
     *
     * @param password 输入的密码
     * @return true:强度太弱
     */
    private boolean passwordIsTooWeak(String password) {
        return password.length() < 6
                || !password.matches("[a-zA-Z0-9]+")  //过滤非数字、非字母
                || !password.matches(".*[a-zA-z].*")   //必须包含字母
                || !password.matches(".*[0-9].*");    // 必须包含数字
    }


}
