package com.cannontech.web.login.access;

import com.cannontech.common.util.Checker;
import com.cannontech.database.data.lite.LiteYukonUser;

public class UrlAccess {
    private AccessType type;
    private String urlPath;
    private Checker<LiteYukonUser> roleIdChecker;
    
    public AccessType getType() {
        return type;
    }
    
    public void setType(AccessType type) {
        this.type = type;
    }
    
    public String getUrlPath() {
        return urlPath;
    }
    
    public void setUrlPath(String path) {
        this.urlPath = path;
    }
    
    public Checker<LiteYukonUser> getRoleIdChecker() {
        return roleIdChecker;
    }
    
    public void setRoleIdChecker(Checker<LiteYukonUser> roleIdChecker) {
        this.roleIdChecker = roleIdChecker;
    }
    
}
