package ru.vladimir.controller;

import java.util.NoSuchElementException;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.data.rest.webmvc.support.ETagDoesntMatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({ IllegalArgumentException.class, ConversionFailedException.class,
            RepositoryConstraintViolationException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequest() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Некорректные параметры запроса.");
    }

    @ExceptionHandler({ NoSuchElementException.class, EntityNotFoundException.class,
            ResourceNotFoundException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFound() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "Запрошенный ресурс не найден.");
    }

    @ExceptionHandler({ DataIntegrityViolationException.class, OptimisticLockingFailureException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleConflict() {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Операция нарушает ограничения данных или конфликтует с их текущим состоянием.");
    }

    @ExceptionHandler(ETagDoesntMatchException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<ProblemDetail> handlePreconditionFailed(ETagDoesntMatchException exception) {
        HttpHeaders headers = exception.getExpectedETag().addTo(new HttpHeaders());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.PRECONDITION_FAILED,
                "Версия ресурса изменилась. Получите актуальные данные и повторите запрос.");
        return new ResponseEntity<>(problem, headers, HttpStatus.PRECONDITION_FAILED);
    }
}
