package com.aiolos.live.dubbo.common.advice;

import com.aiolos.common.enums.errors.ErrorEnum;
import com.aiolos.common.model.response.CommonResponse;
import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@Order(-11)
@RestControllerAdvice
public class RpcExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(RpcExceptionAdvice.class);

    @ExceptionHandler(value = RpcException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public CommonResponse handlerTokenInvalidException(Exception e) {
        if (e instanceof RpcException) {
            log.error("dubbo服务调用异常: {}", e.getMessage(), e);
            if (e.getCause() != null && e.getCause() instanceof TimeoutException) {
                return CommonResponse.error(ErrorEnum.DUBBO_TIMEOUT_EXCEPTION);
            }
            return CommonResponse.error(ErrorEnum.NO_PROVIDER_ERROR);
        } else {
            return CommonResponse.error(ErrorEnum.UNKNOWN_ERROR);
        } 
    }
}
