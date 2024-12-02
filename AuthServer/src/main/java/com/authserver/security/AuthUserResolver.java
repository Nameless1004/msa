package com.authserver.security;

import com.authserver.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthUserResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class);
    }

    @Override
    public AuthUserDetails resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Long userId = Long.valueOf(webRequest.getParameter("X-User-Id"));
        String email = webRequest.getParameter("X-User-Email");
        UserRole role = UserRole.valueOf(webRequest.getParameter("X-User-Role"));

        return new AuthUserDetails(userId, email, role);
    }
}
