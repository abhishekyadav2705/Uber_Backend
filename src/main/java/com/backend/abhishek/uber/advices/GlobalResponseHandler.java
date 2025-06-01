package com.backend.abhishek.uber.advices;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    private static final List<String> ALLOWED_PATHS = List.of("/v3/api-docs", "/actuator");

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        String path = request.getURI().getPath();

        if (ALLOWED_PATHS.stream().anyMatch(path::contains)) {
            return body;
        }

        // Prevent wrapping error responses (which already include 'status' and 'message')
        if (body instanceof Map && ((Map<?, ?>) body).containsKey("status") && ((Map<?, ?>) body).containsKey("message")) {
            return body;
        }

        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("status", "success");
        responseBody.put("path", path);
        responseBody.put("method", request.getMethod().name());
        responseBody.put("data", body);
        responseBody.put("message", "Request was successful");
        responseBody.put("timestamp", LocalDateTime.now().toString());
        responseBody.put("statusCode", 200);


        return responseBody;
    }
}
