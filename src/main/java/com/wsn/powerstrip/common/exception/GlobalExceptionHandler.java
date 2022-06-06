package com.wsn.powerstrip.common.exception;

import com.wsn.powerstrip.common.POJO.DTO.Response;
import com.wsn.powerstrip.device.exception.DeviceDAOException;
import com.wsn.powerstrip.device.exception.LocationException;
import com.wsn.powerstrip.member.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;

/**
 * 全局异常捕获，
 * TODO：这里只是大概写了一下，不同的异常类型应该有不同的处理方案
 * @Author: wangzilinn@gmail.com
 * @Description:
 * @Date: Created in 11:14 PM 6/20/2020
 * @Modified By:wangzilinn@gmail.com
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DeviceDAOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<?> powerStripException(DeviceDAOException deviceDAOException) {
        log.error(deviceDAOException.getMessage());
        log.error(Arrays.toString(deviceDAOException.getStackTrace()));
        return new Response<>(deviceDAOException.getCode(), deviceDAOException.getMessage());
    }

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response<?> userException(UserException userException) {
        log.error(userException.getMessage());
        log.error(Arrays.toString(userException.getStackTrace()));
        return new Response<>(userException.getCode(), userException.getMessage());
    }

    @ExceptionHandler(LocationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response<?> LocationException(LocationException locationException) {
        log.error(locationException.getMessage());
        log.error(Arrays.toString(locationException.getStackTrace()));
        return new Response<>(locationException.getCode(), locationException.getMessage());
    }

    // 前端请求参数错误时异常捕获,像前端返回参数错误
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Response<?> ConstraintViolationException(ConstraintViolationException constraintViolationException) {
        log.error(constraintViolationException.getMessage());
        log.error(Arrays.toString(constraintViolationException.getStackTrace()));
        return new Response<>(403, constraintViolationException.getMessage());
    }


}
