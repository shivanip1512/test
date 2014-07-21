package com.cannontech.web.login.access;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

import com.cannontech.core.authorization.support.AllowDeny;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.util.ServletUtil;

public class UrlAccessCheckerImpl implements UrlAccessChecker {
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private UrlAccessDescriptionParser descriptionParser;
    private String[] urlAccessDescriptions;
    private List<UrlAccess> urlAccessList;
    
    @PostConstruct
    public void init() throws Exception {
        urlAccessList = descriptionParser.parse(urlAccessDescriptions);
    }
    
    @Override
    public boolean hasUrlAccess(HttpServletRequest request) {
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        String urlPath = urlPathHelper.getPathWithinApplication(request);
        return hasUrlAccess(urlPath, user);
    }
    
    @Override
    public boolean hasUrlAccess(final String urlPath, final LiteYukonUser user) {
        for (final UrlAccess urlAccess : urlAccessList) {
            if (isUrlPathMatch(urlPath, urlAccess)) {
                if (isRoleIdMatch(urlAccess.getUserChecker(), user)) {
                    boolean hasUrlAccess = urlAccess.getType().equals(AllowDeny.ALLOW);
                    return hasUrlAccess;
                }
            }
        }
        return false;
    }
    
    private boolean isRoleIdMatch(UserChecker userChecker, LiteYukonUser user) {
        boolean isRoleIdMatch = userChecker.check(user);
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
