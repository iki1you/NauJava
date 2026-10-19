package ru.vladimir.controller;

import java.net.URI;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class RestErrorResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return JacksonJsonHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType,
            MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {
        if (!(response instanceof ServletServerHttpResponse servletResponse)) {
            return body;
        }

        int status = servletResponse.getServletResponse().getStatus();
        if (status < 400 || body instanceof ProblemDetail) {
            return body;
        }

        String detail = status >= 500 ? "Внутренняя ошибка сервера."
                : status == 404 ? "Запрошенный ресурс не найден." : "Некорректный запрос.";
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(status), detail);
        problem.setInstance(URI.create(request.getURI().getRawPath()));
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);
        return problem;
    }
}
