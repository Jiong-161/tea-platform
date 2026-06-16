package com.tea.gateway.config;

import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> businessException(BusinessException e) {
        Result<Void> result = Result.fail(e.getMessage());
        result.setCode(e.getCode() != null ? e.getCode() : 500);
        return result;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> methodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();
        return Result.fail(message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> exception(Exception e) {
        e.printStackTrace();
        return Result.fail("系统异常，请稍后重试");
    }
}