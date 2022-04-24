package com.example.netflix.musicservice.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.xml.sax.SAXException;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class CustomRestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {HttpServerErrorException.class})
    protected ResponseEntity<Object> handleServerErrorException(HttpServerErrorException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getResponseBodyAsString(), ex.getResponseHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(value = {HttpClientErrorException.class})
    protected ResponseEntity<Object> handleClientErrorException(HttpClientErrorException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getResponseBodyAsString(),
                ex.getResponseHeaders(), ex.getStatusCode(), request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest) {
        log.error(ex.getMessage(), ex);
        return super.handleExceptionInternal(ex, body, headers, statusCode, webRequest);
    }
}