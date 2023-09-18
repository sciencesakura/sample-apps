package com.sciencesakura.sample.api;

import com.sciencesakura.sample.domain.BusinessException;
import com.sciencesakura.sample.domain.ConflictException;
import com.sciencesakura.sample.domain.NotFoundException;
import com.sciencesakura.sample.util.Messages;
import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.stream.Streams;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
class ExceptionHandlers extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode,
      WebRequest request) {
    var bd = body instanceof ProblemDetail p ? new Body(new Message(p.getTitle(), p.getDetail())) : body;
    return new ResponseEntity<>(bd, headers, statusCode);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
      HttpStatusCode statusCode, WebRequest request) {
    if (statusCode.is5xxServerError()) {
      log.error("**{}** {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    } else {
      log.warn("**{}** {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
    }
    return super.handleExceptionInternal(ex, body, headers, statusCode, request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
    var messages = Streams.of(ex.getAllErrors())
        .map(e -> new Message(Messages.get(e), ObjectErrorDetails.from(e)))
        .toArray(Message[]::new);
    return handleExceptionInternal(ex, new Body(messages), headers, status, request);
  }

  private record ObjectErrorDetails(String type, String field) implements Serializable {

    private static ObjectErrorDetails from(ObjectError e) {
      return new ObjectErrorDetails(e.getCode(), e instanceof FieldError f ? f.getField() : null);
    }
  }

  @ExceptionHandler
  ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
    var body = new Body(new Message(ex.getMessage(), null));
    return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler
  ResponseEntity<?> handleNotFound(NotFoundException ex, WebRequest request) {
    return handleBusinessException(ex, HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler
  ResponseEntity<?> handleConflict(ConflictException ex, WebRequest request) {
    return handleBusinessException(ex, HttpStatus.CONFLICT, request);
  }

  private ResponseEntity<?> handleBusinessException(BusinessException ex, HttpStatusCode status, WebRequest request) {
    var body = new Body(new Message(ex.getMessage(), ex.getDetails()));
    return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
  }

  private record Body(Message... messages) implements Serializable {

  }

  private record Message(String message, Object details) implements Serializable {

  }
}
