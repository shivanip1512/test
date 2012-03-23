package com.cannontech.selenium.solvents.common.authentication.service;

import java.util.Map;

public interface RoleSeleniumService {
    
    /**
     * This method takes a yukon role name and either clicks on the link with that name or 
     * adds that role to the current selected group or user.
     */
    public void selectYukonRoleForUserOrGroup(String YukonRoleName);
    
    /**
     * This method takes a yukon role name and either clicks on the link with that name or 
     * adds that role to the current selected group or user.  It also updates the role with
     * the supplied input name role property value map.
     */
    public void setupYukonRoleForUserOrGroup(String yukonRoleName, Map<String, String> inputNameToRPValueMap);

}