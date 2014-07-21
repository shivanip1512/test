package com.cannontech.web.login.access;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.authorization.support.AllowDeny;
import com.cannontech.user.checker.RolePropertyUserCheckerFactory;
import com.cannontech.user.checker.UserChecker;


public class UrlAccessDescriptionParser {
    private static final String DELIMITER = "\\|";

    private static final int INDEX_PERMISSIONTYPE = 0;
    private static final int INDEX_URLPATH = 1;
    private static final int INDEX_ROLEIDS = 2;

    private RoleAndPropertyDescriptionService roleAndPropertyDescriptionService;
    
    public List<UrlAccess> parse(String[] descriptions) {
        final List<UrlAccess> descriptionList = new LinkedList<UrlAccess>();
    
        for (final String description : descriptions) {
            UrlAccess permissionUrlPath = parse(description);
            descriptionList.add(permissionUrlPath);
        }
        
        return descriptionList;
    }
    
    public UrlAccess parse(final String description) throws ParseUrlAccessException {
        final String[] split = description.split(DELIMITER);
        checkForValidSyntax(split);
        
        final UrlAccess access = new UrlAccess();
        
        AllowDeny type = getAccessType(split);
        access.setType(type);
        
        String path = getUrlPath(split);
        access.setUrlPath(path);
        
        UserChecker userChecker = getRoleIdChecker(split);
        access.setUserChecker(userChecker);
        
        return access;
    }

    private AllowDeny getAccessType(String[] split) {
        String accessTypeString = split[INDEX_PERMISSIONTYPE].trim();
        AllowDeny type = AllowDeny.valueOf(accessTypeString);
        return type;
    }

    private String getUrlPath(String[] split) {
        String path = split[INDEX_URLPATH].trim();
        return path;
    }
    
    private UserChecker getRoleIdChecker(String[] split) {
        String roleIdDescriptions = split[INDEX_ROLEIDS].trim();
        UserChecker checker = roleAndPropertyDescriptionService.compile(roleIdDescriptions);
        return checker;
    }
    
    private void checkForValidSyntax(String[] split) throws ParseUrlAccessException {
        if (split.length == 0 || split.length != 3) {
            throw new ParseUrlAccessException("Invalid UrlAccess syntax (incorrect number of fields)");
        }        
    }
    
    @Autowired
    public void setRoleAndPropertyDescriptionService(
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        this.roleAndPropertyDescriptionService = roleAndPropertyDescriptionService;
    }
    
}
