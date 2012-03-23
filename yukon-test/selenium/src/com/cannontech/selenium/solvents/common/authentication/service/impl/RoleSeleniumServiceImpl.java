package com.cannontech.selenium.solvents.common.authentication.service.impl;

import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.Duration;

import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.authentication.service.RoleSeleniumService;

public class RoleSeleniumServiceImpl implements RoleSeleniumService {

    private final String addRoleFormAction = "groupEditor/addRole";
    private static final String editRolePropertiesFormAction = "roleEditor/update";
    
    @Override
    public void selectYukonRoleForUserOrGroup(String yukonRoleName) {
        CommonSolvent common = new CommonSolvent();
        
        // Click the yukon role link if it exists
        boolean isYukonRoleLinkPresent = common.isLinkPresent(yukonRoleName, Duration.ZERO);
        if (isYukonRoleLinkPresent) {
            common.clickLinkByName(yukonRoleName);
            return;
        }
        
        // Add new yukon role to group or user
        common.selectDropDownMenu(addRoleFormAction, "newRoleId", yukonRoleName);
        common.clickFormButton(addRoleFormAction, "add");
    }

    @Override
    public void setupYukonRoleForUserOrGroup(String yukonRoleName, Map<String, String> inputNameToRPValueMap) {
        selectYukonRoleForUserOrGroup(yukonRoleName);
        
        setRolePropertyFields(inputNameToRPValueMap);
    }
    
    private void setRolePropertyFields(Map<String, String> inputNameToRPValueMap) {
        CommonSolvent common = new CommonSolvent();

        // Setup
        for (Entry<String, String> inputNameToRPValueEntity : inputNameToRPValueMap.entrySet()) {
            // Check to see if the field is a select.  If it is set it and continue.
            if (common.isSelectDropDownPresent(editRolePropertiesFormAction, inputNameToRPValueEntity.getKey())) {
                common.selectDropDownMenu(editRolePropertiesFormAction, inputNameToRPValueEntity.getKey(), inputNameToRPValueEntity.getValue());
            
            // Check to see if the field is an input field.  If it is set it.
            } else if (common.isEnterInputTextPresent(editRolePropertiesFormAction, inputNameToRPValueEntity.getKey())) {
                common.enterInputText(editRolePropertiesFormAction, inputNameToRPValueEntity.getKey(), inputNameToRPValueEntity.getValue());
            }
        }
        
        common.clickFormButton(editRolePropertiesFormAction, "save");
    }
}