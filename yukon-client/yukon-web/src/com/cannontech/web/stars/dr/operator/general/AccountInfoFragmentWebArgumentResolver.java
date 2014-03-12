package com.cannontech.web.stars.dr.operator.general;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class AccountInfoFragmentWebArgumentResolver extends AccountInfoFragmentArgumentResolver
        implements WebArgumentResolver {
    @Override
    public Object resolveArgument(MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        if (supportsParameter(parameter)) {
            super.resolveArgument(webRequest);
        }
        return UNRESOLVED;
    }
}
