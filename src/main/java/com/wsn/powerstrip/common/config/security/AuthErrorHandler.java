package com.wsn.powerstrip.common.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsn.powerstrip.common.POJO.DTO.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当认证失败，系统会抛出认证失败异常，可以配置我们自己的认证失败处理类，同样鉴权失败也可以配置我们自己的失败处理类。
 * JwtAuthError继承AuthenticationEntryPoint（认证失败接口）、AccessDeniedHandler（鉴权失败接口），
 * 重写了这2个接口类的失败处理方法，其实JwtAuthError可以分为2个类，我们合二为一了
 */
@Component
@Slf4j
public class AuthErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    @Autowired
    ObjectMapper jsonMapper;


    /**
     * 认证失败处理，返回401 json数据
     *
     * @param request       .
     * @param response      .
     * @param authException .
     * @throws IOException .
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.info("认证失败处理，返回401");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(jsonMapper.writeValueAsString(new Response<>(401, "用户名或密码错误或无效token")));
    }

    /**
     * 鉴权失败处理，返回403 json数据
     *
     * @param request               .
     * @param response              .
     * @param accessDeniedException .
     * @throws IOException .
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.info("鉴权失败处理，返回403");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(jsonMapper.writeValueAsString(new Response<>(403, "当前用户无此操作权限")));
    }
}
