package com.cannontech.web.login;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class EnumWebArgumentResolver extends EnumArgumentResolver implements WebArgumentResolver {
    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        if (super.supportsParameter(methodParameter)) {
            super.resolveArgument(methodParameter, webRequest);
        }
        return UNRESOLVED;
    }
}
