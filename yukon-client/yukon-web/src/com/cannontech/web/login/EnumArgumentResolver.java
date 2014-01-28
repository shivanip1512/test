package com.cannontech.web.login;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

public class EnumArgumentResolver {

    protected boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isEnum();
    }

    protected Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        String parameter = webRequest.getParameter(methodParameter.getParameterName());
        @SuppressWarnings("rawtypes")
        Class parameterType = methodParameter.getParameterType();
        @SuppressWarnings("unchecked")
        Object result = Enum.valueOf(parameterType, parameter);
        return result;
    }

}
