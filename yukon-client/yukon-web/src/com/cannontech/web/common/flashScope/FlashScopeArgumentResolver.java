package com.cannontech.web.common.flashScope;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class FlashScopeArgumentResolver implements WebArgumentResolver {

	@Override
    public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
        
		Class<?> parameterType = methodParameter.getParameterType();
        if (parameterType.isAssignableFrom(FlashScope.class)) {
            HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
            return new FlashScope(nativeRequest);
        }
        
        return UNRESOLVED;
    }
}
