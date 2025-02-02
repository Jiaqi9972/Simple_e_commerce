package me.findthepeach.common.response.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.model.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseResultHandler implements ResponseBodyAdvice<Object> {
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // handle return type which is not equals to ApiResponse
        if (returnType.getParameterType().equals(ApiResponse.class)) {
            return false;
        }

        if (returnType.getContainingClass().isAnnotationPresent(RestControllerAdvice.class)) {
            return false;
        }

        // swagger
        String packageName = returnType.getDeclaringClass().getPackage().getName();
        return !packageName.contains("springdoc") &&
                !packageName.contains("swagger") &&
                !packageName.contains("openapi");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // swagger
        String path = request.getURI().getPath();
        if (path.contains("/v3/api-docs") ||
                path.contains("/swagger-ui/") ||
                path.contains("/swagger-resources")) {
            return body;
        }

        // handle ApiResponse
        if (body instanceof ApiResponse) {
            return body;
        }

        // handle null
        if (body == null) {
            return ApiResponse.success(null);
        }

        // handle String
        if (body instanceof String) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(ApiResponse.success(body));
            } catch (JsonProcessingException e) {
                log.error("Error processing string response", e);
                return ApiResponse.error(ReturnCode.INTERNAL_ERROR);
            }
        }

        // handle other
        return ApiResponse.success(body);
    }
}
