package com.cannontech.web.login.access;

import com.cannontech.core.authorization.support.AllowDeny;
import com.cannontech.user.checker.UserChecker;

public class UrlAccess {
    private AllowDeny type;
    private String urlPath;
    private UserChecker userChecker;
    
    public AllowDeny getType() {
        return type;
    }
    
    public void setType(AllowDeny type) {
        this.type = type;
    }
    
    public String getUrlPath() {
        return urlPath;
    }
    
    public void setUrlPath(String path) {
        this.urlPath = path;
    }

    public UserChecker getUserChecker() {
        return userChecker;
    }

    public void setUserChecker(UserChecker userChecker) {
        this.userChecker = userChecker;
    }
    
}
