package me.findthepeach.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.annotation.InternalApiAuth;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.SystemException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class InternalApiAuthInterceptor implements HandlerInterceptor {

    // TODO config internal token
//    @Value("${internal.service.token}")
    private final String internalServiceToken = "internal-token-123";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            InternalApiAuth auth = handlerMethod.getMethodAnnotation(InternalApiAuth.class);
            if (auth != null) {
                String token = request.getHeader("Internal-Service-Token");
                if (!internalServiceToken.equals(token)) {
                    throw new SystemException(ReturnCode.INTERNAL_ERROR);
                }
            }
        }
        return true;
    }
}
