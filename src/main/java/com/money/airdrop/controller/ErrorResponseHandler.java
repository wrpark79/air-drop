package com.money.airdrop.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorResponseHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public Object handleBadRequest(Exception e, WebRequest request) {
        return handleExceptionInternal(e, getAttributes(e, HttpStatus.BAD_REQUEST),
            new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {Exception.class})
    public Object handleServerError(Exception e, WebRequest request) {
        return handleExceptionInternal(e, getAttributes(e, HttpStatus.INTERNAL_SERVER_ERROR),
            new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private Map<String, String> getAttributes(Exception e, HttpStatus status) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("status", String.valueOf(status.value()));
        attributes.put("error", status.getReasonPhrase());
        attributes.put("message", e.getMessage());
        return attributes;
    }
}