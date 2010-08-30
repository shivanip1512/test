package com.cannontech.web.login;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class EnumArgumentResolver implements WebArgumentResolver {

    @SuppressWarnings("unchecked")
    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest)
        throws Exception {
        Class parameterType = methodParameter.getParameterType();
        if (parameterType.isEnum()) {
            String parameter = webRequest.getParameter(methodParameter.getParameterName());
            Object result = Enum.valueOf(parameterType, parameter);
            return result;
        }
        
        return UNRESOLVED;
    }

}
