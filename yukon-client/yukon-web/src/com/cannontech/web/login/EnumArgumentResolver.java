package com.cannontech.web.login;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class EnumArgumentResolver implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isEnum();
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        String parameter = webRequest.getParameter(methodParameter.getParameterName());
        @SuppressWarnings("rawtypes")
        Class parameterType = methodParameter.getParameterType();
        @SuppressWarnings("unchecked")
        Object result = Enum.valueOf(parameterType, StringEscapeUtils.escapeXml11(parameter));
        return result;
    }
}
