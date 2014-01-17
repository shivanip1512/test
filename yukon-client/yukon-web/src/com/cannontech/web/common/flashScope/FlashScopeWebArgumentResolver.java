package com.cannontech.web.common.flashScope;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class FlashScopeWebArgumentResolver extends FlashScopeArgumentResolver
    implements WebArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
        if (supportsParameter(parameter)) {
            return super.resolveArgument(parameter, webRequest);
        }
        return UNRESOLVED;
    }
}
