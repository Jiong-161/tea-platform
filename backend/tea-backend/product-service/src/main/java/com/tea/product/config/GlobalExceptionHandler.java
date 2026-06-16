package com.tea.product.config;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.tea.common.exception.BusinessException;
import com.tea.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * product-service 全局异常处理器
 *
 * 优化点：
 * 1. 新增 Sentinel BlockException 分级捕获（限流/熔断/热点限流）
 * 2. 统一日志记录，便于排查问题
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ========== Sentinel 限流/熔断/热点参数限流异常 ==========
    @ExceptionHandler(BlockException.class)
    public Result<Void> handleBlockException(BlockException e) {
        if (e instanceof FlowException) {
            log.warn("[Sentinel限流] 接口被限流: {}", e.getRule().getResource());
            Result<Void> result = new Result<>();
            result.setCode(429);
            result.setMessage("系统繁忙，请稍后再试");
            return result;
        } else if (e instanceof DegradeException) {
            log.error("[Sentinel熔断] 服务已降级: resource={}", e.getRule().getResource());
            Result<Void> result = new Result<>();
            result.setCode(503);
            result.setMessage("服务繁忙，已自动降级");
            return result;
        } else if (e instanceof ParamFlowException) {
            log.warn("[Sentinel热点限流] 参数被限流: {}", e.getMessage());
            Result<Void> result = new Result<>();
            result.setCode(429);
            result.setMessage("操作过于频繁，请稍后再试");
            return result;
        } else {
            log.error("[Sentinel拦截] 未分类BlockException", e);
            return Result.fail("请求被限制，请稍后再试");
        }
    }

    // ========== 业务异常（原有逻辑）==========
    @ExceptionHandler(BusinessException.class)
    public Result<Void> businessException(BusinessException e) {
        Result<Void> result = Result.fail(e.getMessage());
        result.setCode(e.getCode() != null ? e.getCode() : 500);
        return result;
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> exception(Exception e) {
        log.error("[系统异常] 未捕获异常", e);
        return Result.fail("系统异常，请稍后重试");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> methodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return Result.fail(message);
    }
}