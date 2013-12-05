package com.cannontech.web.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.web.util.YukonUserContextResolver;

/**
 * Interceptor used for authenticating remote requests.  Will not create a session.
 */
public class RemoteRequestInterceptor extends HandlerInterceptorAdapter {
    
	@Autowired private YukonUserContextResolver userContextResolver;
	private UserChecker userChecker;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        YukonUserContext context = userContextResolver.resolveContext(request);
        userChecker.verify(context.getYukonUser());
        return true;
    }

    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }
}