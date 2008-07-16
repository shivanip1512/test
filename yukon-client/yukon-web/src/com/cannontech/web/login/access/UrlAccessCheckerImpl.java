package com.cannontech.web.login.access;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.common.util.Checker;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

public class UrlAccessCheckerImpl implements UrlAccessChecker,InitializingBean {
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private UrlAccessDescriptionParser descriptionParser;
    private String[] urlAccessDescriptions;
    private List<UrlAccess> urlAccessList;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        urlAccessList = descriptionParser.parse(urlAccessDescriptions);
    }
    
    @Override
    public boolean hasUrlAccess(HttpServletRequest request) {
        final LiteYukonUser user = ServletUtil.getYukonUser(request);
        final String path = urlPathHelper.getPathWithinApplication(request);

        try {
            for (final UrlAccess urlAccess : urlAccessList) {
                if (isUrlPathMatch(path, urlAccess)) {
                    if (isRoleIdMatch(urlAccess.getRoleIdChecker(), user)) {
                        boolean hasUrlAccess = urlAccess.getType().equals(AccessType.ALLOW);
                        return hasUrlAccess;
                    }
                }
            }
        } catch (ParseUrlAccessException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
    
    private boolean isRoleIdMatch(Checker<LiteYukonUser> roleIdChecker, LiteYukonUser user) {
        boolean isRoleIdMatch = roleIdChecker.check(user);
        return isRoleIdMatch;
    }
    
    private boolean isUrlPathMatch(String path, UrlAccess urlAccess) {
        String urlPath = urlAccess.getUrlPath();
        boolean isUrlPathMatch = (pathMatcher.match(urlPath, path));
        return isUrlPathMatch;
    }
    
    public void setUrlAccessDescriptions(String[] urlAccessDescriptions) {
        this.urlAccessDescriptions = urlAccessDescriptions;
    }

    public void setDescriptionParser(
            UrlAccessDescriptionParser descriptionParser) {
        this.descriptionParser = descriptionParser;
    }
    
}
