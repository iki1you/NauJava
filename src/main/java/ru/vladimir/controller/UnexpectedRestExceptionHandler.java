package ru.vladimir.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "ru.vladimir.controller")
@Order(Ordered.LOWEST_PRECEDENCE)
public class UnexpectedRestExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UnexpectedRestExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleUnexpectedException(Exception exception) {
        LOG.error("Unexpected error during REST request processing", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера.");
    }
}
