package com.cannontech.web.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.util.YukonUserContextResolver;

/**
 * This interceptor prevents non-operator CI users from accessing support and sitemap pages.
 */
public class CiSecurityInterceptor extends HandlerInterceptorAdapter {

    @Autowired private YukonUserContextResolver userContextResolver;
    @Autowired private RolePropertyDao rolePropertyDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LiteYukonUser user = userContextResolver.resolveContext(request).getYukonUser();

        boolean hasCiRole = rolePropertyDao.checkRole(YukonRole.CI_CURTAILMENT, user);
        boolean isCiOperator = rolePropertyDao.checkProperty(YukonRoleProperty.CURTAILMENT_IS_OPERATOR, user);

        if (hasCiRole && !isCiOperator) {
            throw new NotAuthorizedException("User " + user + " is not authorized to access this page.");
        }
        return true;
    }

}
