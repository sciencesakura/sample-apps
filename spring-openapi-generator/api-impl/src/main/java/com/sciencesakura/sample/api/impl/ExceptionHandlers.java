package com.sciencesakura.sample.api.impl;

import com.sciencesakura.sample.api.model.ErrorResponse;
import com.sciencesakura.sample.domain.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class ExceptionHandlers extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode,
      WebRequest request) {
    var bd = body instanceof ProblemDetail p ? new ErrorResponse(p.getTitle()) : body;
    return super.createResponseEntity(bd, headers, statusCode, request);
  }

  @ExceptionHandler
  ResponseEntity<?> handleNotFound(NotFoundException ex, WebRequest request) {
    var body = new ErrorResponse(ex.getMessage());
    body.setDetails(ex.getDetails());
    return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler
  ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
    var body = new ErrorResponse(ex.getMessage());
    return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, request);
  }
}
