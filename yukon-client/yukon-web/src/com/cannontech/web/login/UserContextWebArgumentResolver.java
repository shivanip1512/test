package com.cannontech.web.login;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;


public class UserContextWebArgumentResolver extends UserContextArgumentResolver implements WebArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest)
            throws Exception {
        return super.resolveArgument(methodParameter, webRequest);
    }

}
