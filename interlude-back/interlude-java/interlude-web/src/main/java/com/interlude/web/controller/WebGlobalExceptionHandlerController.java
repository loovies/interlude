package com.interlude.web.controller;

import com.interlude.entity.vo.ResponseVO;
import com.interlude.enums.ResponseCodeEnum;
import com.interlude.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * Global exception handler for web APIs.
 */
@RestControllerAdvice
public class WebGlobalExceptionHandlerController extends WebBaseController {

    private static final Logger logger = LoggerFactory.getLogger(WebGlobalExceptionHandlerController.class);

    /**
     * Handle business exceptions.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseVO<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.warn("request business error, url={}, msg={}", request.getRequestURL(), e.getMessage());
        Integer code = e.getCode() == null ? ResponseCodeEnum.CODE_600.getCode() : e.getCode();
        return buildErrorResponse(code, e.getMessage());
    }

    /**
     * Handle @RequestBody validation exceptions.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseVO<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                               HttpServletRequest request) {
        logger.warn("request validate error, url={}", request.getRequestURL(), e);
        return buildErrorResponse(ResponseCodeEnum.CODE_600.getCode(), extractValidationMessage(e));
    }

    /**
     * Handle bind, constraint, and JSON parse exceptions.
     */
    @ExceptionHandler({BindException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class})
    public ResponseVO<?> handleRequestException(Exception e, HttpServletRequest request) {
        logger.warn("request bind error, url={}", request.getRequestURL(), e);
        return buildErrorResponse(ResponseCodeEnum.CODE_600.getCode(), extractValidationMessage(e));
    }

    /**
     * Handle duplicate key exceptions.
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseVO<?> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        logger.warn("request duplicate error, url={}", request.getRequestURL(), e);
        return buildErrorResponse(ResponseCodeEnum.CODE_601.getCode(), ResponseCodeEnum.CODE_601.getMsg());
    }

    /**
     * Handle not-found route exceptions.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseVO<?> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        logger.warn("request not found, url={}", request.getRequestURL(), e);
        return buildErrorResponse(ResponseCodeEnum.CODE_404.getCode(), ResponseCodeEnum.CODE_404.getMsg());
    }

    /**
     * Handle all uncaught exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseVO<?> handleException(Exception e, HttpServletRequest request) {
        logger.error("request error, url={}", request.getRequestURL(), e);
        return buildErrorResponse(ResponseCodeEnum.CODE_500.getCode(), ResponseCodeEnum.CODE_500.getMsg());
    }

    /**
     * Build standard error response.
     */
    private ResponseVO<?> buildErrorResponse(Integer code, String message) {
        ResponseVO<Object> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUS_ERROR);
        responseVO.setCode(code);
        responseVO.setInfo(message);
        responseVO.setData(null);
        return responseVO;
    }

    /**
     * Extract readable validation message from known exception types.
     */
    private String extractValidationMessage(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) exception;
            if (e.getBindingResult().getFieldError() != null) {
                return e.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        if (exception instanceof BindException) {
            BindException e = (BindException) exception;
            if (e.getBindingResult().getFieldError() != null) {
                return e.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException e = (ConstraintViolationException) exception;
            if (e.getConstraintViolations() != null && !e.getConstraintViolations().isEmpty()) {
                return e.getConstraintViolations().iterator().next().getMessage();
            }
        }
        return ResponseCodeEnum.CODE_600.getMsg();
    }
}
