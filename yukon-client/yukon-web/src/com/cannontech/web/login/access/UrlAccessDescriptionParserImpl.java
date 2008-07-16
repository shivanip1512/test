package com.cannontech.web.login.access;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.Checker;
import com.cannontech.core.service.RoleAndPropertyDescriptionService;
import com.cannontech.database.data.lite.LiteYukonUser;


public class UrlAccessDescriptionParserImpl implements UrlAccessDescriptionParser {
    private static final String DELIMITER = "\\|";
    private static final String DELIMITER_ROLEIDS = ",";
    
    private static final int INDEX_PERMISSIONTYPE = 0;
    private static final int INDEX_URLPATH = 1;
    private static final int INDEX_ROLEIDS = 2;

    private RoleAndPropertyDescriptionService descriptionService;
    
    @Override
    public List<UrlAccess> parse(String[] descriptions) {
        final List<UrlAccess> descriptionList = new LinkedList<UrlAccess>();
    
        for (final String description : descriptions) {
            UrlAccess permissionUrlPath = parse(description);
            descriptionList.add(permissionUrlPath);
        }
        
        return descriptionList;
    }
    
    @Override
    public UrlAccess parse(final String description) throws ParseUrlAccessException {
        final String[] split = description.split(DELIMITER);
        checkForValidSyntax(split);
        
        final UrlAccess access = new UrlAccess();
        
        AccessType type = getAccessType(split);
        access.setType(type);
        
        String path = getUrlPath(split);
        access.setUrlPath(path);
        
        Checker<LiteYukonUser> roleIdChecker = getRoleIdChecker(split);
        access.setRoleIdChecker(roleIdChecker);
        
        return access;
    }

    private AccessType getAccessType(String[] split) {
        String accessTypeString = split[INDEX_PERMISSIONTYPE].trim();
        AccessType type = AccessType.valueOf(accessTypeString);
        return type;
    }

    private String getUrlPath(String[] split) {
        String path = split[INDEX_URLPATH].trim();
        return path;
    }
    
    private Checker<LiteYukonUser> getRoleIdChecker(String[] split) {
        String roleIdDescriptions = split[INDEX_ROLEIDS].trim();
        final String[] descriptionSplit = roleIdDescriptions.split(DELIMITER_ROLEIDS);
        
        return new Checker<LiteYukonUser>() {
            @Override
            public boolean check(LiteYukonUser user) {
                
                for (final String description : descriptionSplit) {
                    boolean isValidCheck = descriptionService.checkIfAtLeaseOneExists(description, user);
                    if (isValidCheck) return true;
                }
                return false;
            }
        };
    }
    
    private void checkForValidSyntax(String[] split) throws ParseUrlAccessException {
        if (split.length == 0 || split.length != 3) {
            throw new ParseUrlAccessException("Invalid UrlAccess syntax (incorrect number of fields)");
        }        
    }
    
    @Autowired
    public void setDescriptionService(
            RoleAndPropertyDescriptionService descriptionService) {
        this.descriptionService = descriptionService;
    }
    
}
