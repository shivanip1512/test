package com.cannontech.web.common.flashScope;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

public class FlashScopeArgumentResolver {
    protected boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(FlashScope.class);
    }

    protected Object resolveArgument(NativeWebRequest webRequest) throws Exception {
        HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
        return new FlashScope(nativeRequest);
    }
}
