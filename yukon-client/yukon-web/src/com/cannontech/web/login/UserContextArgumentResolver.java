package com.cannontech.web.login;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.api.token.AuthenticationException;
import com.cannontech.web.api.token.TokenHelper;

public class UserContextArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired private YukonUserDao userDao;
    
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        return parameterType.isAssignableFrom(YukonUserContext.class) 
                || parameterType.isAssignableFrom(LiteYukonUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
            ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest nativeHttpRequest = (HttpServletRequest) webRequest.getNativeRequest();

        try {
            String authToken = TokenHelper.resolveToken(nativeHttpRequest);
            String userId = TokenHelper.getUserId(authToken);
            return userDao.getLiteYukonUser(Integer.valueOf(userId));
        } catch (AuthenticationException e) {}

        Class<?> parameterType = methodParameter.getParameterType();
        if (parameterType.isAssignableFrom(YukonUserContext.class)) {
            return YukonUserContextUtils.getYukonUserContext(nativeHttpRequest);
        }
        if (parameterType.isAssignableFrom(LiteYukonUser.class)) {
            ServletRequest nativeRequest = (ServletRequest) webRequest.getNativeRequest();
            return ServletUtil.getYukonUser(nativeRequest);
        }

        return WebArgumentResolver.UNRESOLVED;
    }
}
