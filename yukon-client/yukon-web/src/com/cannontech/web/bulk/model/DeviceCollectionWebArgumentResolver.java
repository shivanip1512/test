package com.cannontech.web.bulk.model;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class DeviceCollectionWebArgumentResolver extends DeviceCollectionArgumentResolver
        implements WebArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        if (supportsParameter(methodParameter)) {
            return super.resolveArgument(methodParameter, webRequest);
        }
        return UNRESOLVED;
    }

}