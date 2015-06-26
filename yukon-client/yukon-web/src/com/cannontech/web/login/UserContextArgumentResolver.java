package com.cannontech.web.login;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class UserContextArgumentResolver implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(YukonUserContext.class) 
                || parameterType.isAssignableFrom(LiteYukonUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        Class<?> parameterType = methodParameter.getParameterType();
        if (parameterType.isAssignableFrom(YukonUserContext.class)) {
            HttpServletRequest nativeRequest = (HttpServletRequest) webRequest.getNativeRequest();
            return YukonUserContextUtils.getYukonUserContext(nativeRequest);
        }

        if (parameterType.isAssignableFrom(LiteYukonUser.class)) {
            ServletRequest nativeRequest = (ServletRequest) webRequest.getNativeRequest();
            return ServletUtil.getYukonUser(nativeRequest);
        }

        return WebArgumentResolver.UNRESOLVED;
    }
}
