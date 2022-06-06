package com.wsn.powerstrip.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsn.powerstrip.common.annotation.WebLog;
import com.wsn.powerstrip.member.POJO.DO.OperationRecord;
import com.wsn.powerstrip.member.POJO.DO.User;
import com.wsn.powerstrip.member.service.OperationRecordService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author 犬小哈 （微信号：小哈学Java）
 * @Modified wangzilinn@gmail.com
 **/
@Aspect
@Component
@Profile({"dev", "prod"})
@Slf4j
public class WebLogAspect {
    final private ObjectMapper objectMapper;
    final private OperationRecordService operationRecordService;

    /**
     * 换行符
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public WebLogAspect(ObjectMapper objectMapper, OperationRecordService operationRecordService) {
        this.objectMapper = objectMapper;
        this.operationRecordService = operationRecordService;
    }

    public ThreadLocal<StringBuilder> printString = new ThreadLocal<>();

    /**
     * @return void
     * @Author wangzilin
     * @Description 在所有的public方法, 以Response为返回值, 任意方法名, 任意参数的前面插入weblog
     * @Date 4:43 PM 5/15/2020
     * @Param []
     **/
    @Pointcut("@annotation(com.wsn.powerstrip.common.annotation.WebLog)")
    public void webLog() {
    }

    /**
     * 在切点之前织入
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        printString.get()
                .append("\n========================================== Start ==========================================\n")
                .append("URL            : ").append(request.getRequestURL().toString()).append("\n")
                .append("HTTP Method    : ").append(request.getMethod()).append("\n")
                .append("Class Method   : ").append(joinPoint.getSignature().getDeclaringTypeName()).append(".").append(joinPoint.getSignature().getName()).append("\n")
                .append("IP             : ").append(request.getRemoteAddr()).append("\n")
                .append("Request Args   : ").append(Arrays.toString(joinPoint.getArgs())).append("\n");
    }

    /**
     * 环绕
     * 打印日志，并根据WebLog的描述信息判断是否记录用户操作，description为空则不记录用户操作，否则记录并写库
     * @param proceedingJoinPoint
     * @return 切点执行结果
     * @throws Throwable
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        printString.set(new StringBuilder());
        Object result = proceedingJoinPoint.proceed();
        //在2.3.4.RELEASE版本之后, @around的后半部分在@after之后执行
        // 打印出参
        try {
            String args = objectMapper.writeValueAsString(result);
            if (args.length() > 100) {
                args = args.substring(0, 100);
            }
            printString.get().append("Response Args  : ").append(args).append("\n");
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            printString.get().append("Response Args  : ").append("出参反序列化失败").append("\n");
        }
        // 执行耗时
        printString.get()
                .append("Time-Consuming : ").append(System.currentTimeMillis() - startTime).append("\n")
                .append("=========================================== End ===========================================\n");
        log.info(printString.get().toString());
        printString.remove();

        //下面开始记录用户行为.如果有描述，就表示需要记录用户的操作，否则不作记录
        String operationDescription = getAspectLogDescription(proceedingJoinPoint);
        if (!operationDescription.equals("")){
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
            OperationRecord operationRecord = OperationRecord.builder()
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role(user.getRole())
                    .ip(request.getRemoteAddr())
                    .operation(operationDescription)
                    .operationDate(new Date())
                    .build();

            operationRecordService.addRecord(operationRecord);
        }



        return result;
    }


    /**
     * 获取切面注解的描述
     *
     * @param joinPoint 切点
     * @return 描述信息
     * @throws Exception
     */
    public String getAspectLogDescription(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        StringBuilder description = new StringBuilder("");
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description.append(method.getAnnotation(WebLog.class).description());
                    break;
                }
            }
        }
        return description.toString();
    }

}
