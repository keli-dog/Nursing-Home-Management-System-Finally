package com.ew.gerocomium.common.config.exception;

import com.ew.gerocomium.common.constant.CodeEnum;
import com.ew.gerocomium.common.constant.ExceptionEnum;
import com.ew.gerocomium.common.util.ResponseUtil;
import com.ew.gerocomium.dao.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class BusinessRuntimeExceptionAdvice {
    @ExceptionHandler({Exception.class})
    public void exceptionHandler(Exception ex, HttpServletResponse response, HttpServletRequest request) {
        String errorType;
        Integer errorCode;
        String errorMsg;
        if (ex instanceof BusinessRuntimeException) {
            errorType = "业务异常";
            // AssertUtils断言抛出的自定义业务异常
            errorCode = ((BusinessRuntimeException) ex).getCode();
            errorMsg = ((BusinessRuntimeException) ex).getMsg();
        } else if (ex instanceof NoHandlerFoundException) {
            errorType = "无访问资源";
            errorCode = ExceptionEnum.NOT_EXIST.getCode();
            errorMsg = ExceptionEnum.NOT_EXIST.getMsg();
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            errorType = "请求方式错误";
            errorCode = ExceptionEnum.METHOD_ERROR.getCode();
            errorMsg = ExceptionEnum.METHOD_ERROR.getMsg();
            // MissingServletRequestParameterException -> 未传必传参数
            // MethodArgumentTypeMismatchException -> 参数类型错误
        } else if (ex instanceof MissingServletRequestParameterException || ex instanceof MethodArgumentTypeMismatchException) {
            errorType = "请求参数异常";
            errorCode = ExceptionEnum.PARAM_ERROR.getCode();
            errorMsg = ExceptionEnum.PARAM_ERROR.getMsg();
            // InternalAuthenticationServiceException -> 账号未注册
            // BadCredentialsException -> 密码错误
        } else if (ex instanceof InternalAuthenticationServiceException || ex instanceof BadCredentialsException) {
            errorType = "认证失败";
            errorCode = ExceptionEnum.CERTIFICATION_ERROR.getCode();
            errorMsg = ExceptionEnum.CERTIFICATION_ERROR.getMsg();
        } else if (ex instanceof AccessDeniedException) {
            errorType = "权限不足";
            errorCode = ExceptionEnum.AUTH_ERROR.getCode();
            errorMsg = ExceptionEnum.AUTH_ERROR.getMsg();
        } else {
            errorType = "其他异常";
            errorCode = CodeEnum.ERROR.getCode();
            String message = ex.getMessage();
            boolean checkMsg = false;
            for (ExceptionEnum exceptionEnum : ExceptionEnum.values()) {
                if (Objects.equals(exceptionEnum.getMsg(), message)) {
                    checkMsg = true;
                    break;
                }
            }
            errorMsg = checkMsg ? message : ExceptionEnum.SYS_ERROR.getMsg();
        }
        if (log.isInfoEnabled()) {
            log.info(errorType);
            ex.printStackTrace();
        }
        ResponseUtil.resultOut(response, Result.error(errorCode, errorMsg));
    }
}
