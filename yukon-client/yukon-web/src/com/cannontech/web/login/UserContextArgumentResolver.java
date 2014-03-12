package com.cannontech.web.login;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class UserContextArgumentResolver {

    protected boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(YukonUserContext.class)
            || parameterType.isAssignableFrom(LiteYukonUser.class);
    }

    protected Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
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
