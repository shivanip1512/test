package com.cannontech.web.login;

import java.util.Optional;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.api.token.AuthenticationException;
import com.cannontech.web.api.token.TokenHelper;
import com.cannontech.web.util.YukonUserContextConstructor;

public class UserContextArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger log = YukonLogManager.getLogger(UserContextArgumentResolver.class);
    
    @Autowired private YukonUserDao userDao;
    @Autowired private YukonUserContextConstructor userContextConstructor;
    
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
            Optional<LiteYukonUser> user = getUser(webRequest);
            return user.isPresent() ? userContextConstructor.resolveContext(user.get(),
                            (HttpServletRequest) webRequest.getNativeRequest())
                    : YukonUserContextUtils.getYukonUserContext((HttpServletRequest) webRequest.getNativeRequest());
        }
        if (parameterType.isAssignableFrom(LiteYukonUser.class)) {
            Optional<LiteYukonUser> user = getUser(webRequest);
            return user.isPresent() ? user.get()
                    : ServletUtil.getYukonUser((ServletRequest) webRequest.getNativeRequest());
        }
        return WebArgumentResolver.UNRESOLVED;
    }

    /**
     * Return LiteYukonUser for api calls from token else return empty.
     */
    private Optional<LiteYukonUser> getUser(NativeWebRequest webRequest) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String requestURI = request.getRequestURI();
        return isApiRequest(requestURI) ? getUserFromToken(request) : Optional.empty();
    }

    private Optional<LiteYukonUser> getUserFromToken(HttpServletRequest request) {
        try {
            String authToken = TokenHelper.resolveToken(request);
            String userId = TokenHelper.getUserId(authToken);
            return Optional.of(userDao.getLiteYukonUser(Integer.valueOf(userId)));
        } catch (AuthenticationException e) {
            log.debug("Failed to authenticate user via token.", e);
            return Optional.empty();
        }
    }

    /**
     * Return true if requestURI contains "/api" in the request. Return false if requestURI = null.
     */
    private boolean isApiRequest(String requestURI) {
        return requestURI == null ? false : requestURI.contains("/api");
    }
}
