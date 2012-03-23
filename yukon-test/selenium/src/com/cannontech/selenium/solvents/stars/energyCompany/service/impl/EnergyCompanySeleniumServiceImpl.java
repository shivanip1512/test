package com.cannontech.selenium.solvents.stars.energyCompany.service.impl;

import java.util.Map;

import org.junit.Assert;

import com.cannontech.selenium.solvents.common.CommonSolvent;
import com.cannontech.selenium.solvents.common.PopupMenuSolvent;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory;
import com.cannontech.selenium.solvents.common.Pickers.PickerFactory.PickerType;
import com.cannontech.selenium.solvents.common.authentication.service.RoleSeleniumService;
import com.cannontech.selenium.solvents.common.authentication.service.impl.RoleSeleniumServiceImpl;
import com.cannontech.selenium.solvents.common.userLogin.model.Login;
import com.cannontech.selenium.solvents.stars.energyCompany.model.CreateEnergyCompany;
import com.cannontech.selenium.solvents.stars.energyCompany.service.EnergyCompanySeleniumService;

public class EnergyCompanySeleniumServiceImpl implements EnergyCompanySeleniumService {

    private static final String createECFormAction = "energyCompany/create";
    private static final String editECFormAction = "update";
    private static final String deleteECFormAction = "delete";
    
    private RoleSeleniumService roleSeleniumService = new RoleSeleniumServiceImpl();
    
    @Override
    public void createEnergyCompany(CreateEnergyCompany createEnergyCompany) {
        CommonSolvent common = new CommonSolvent();
        
        common.clickLinkByName("System Administration");
        common.clickLinkByName("Energy Company");
        Assert.assertEquals("System Administration: Energy Companies", common.getPageTitle());
        common.clickFormButton("energyCompany/new", "create");

        Assert.assertEquals("System Administration: Create Energy Company", common.getPageTitle());
        common.enterInputText(createECFormAction, "name", createEnergyCompany.getCompanyName());
        common.enterInputText(createECFormAction, "email", createEnergyCompany.getEmail());
        common.selectDropDownMenu(createECFormAction, "defaultRouteId", createEnergyCompany.getDefaultRoute());
        
        Login energyCompanyLogin = createEnergyCompany.getChangeLogin();
        common.enterInputText(createECFormAction, "adminUsername", energyCompanyLogin.getUsername());
        common.enterInputText(createECFormAction, "adminPassword1", energyCompanyLogin.getPassword1());
        common.enterInputText(createECFormAction, "adminPassword2", energyCompanyLogin.getPassword2());
        
        PopupMenuSolvent primaryPopup = PickerFactory.createPopupMenuSolvent("primaryOperatorGroup", PickerType.SingleSelect);
        primaryPopup.openPickerPopup();
        primaryPopup.enterFilterText(createEnergyCompany.getPrimaryOperatorGroup());
        primaryPopup.clickMenuItem(createEnergyCompany.getPrimaryOperatorGroup());
        
        // Added the additional operator groups
        if (createEnergyCompany.getAdditionalOperatorGroups().size() > 0) {
            PopupMenuSolvent adtlPopup = PickerFactory.createPopupMenuSolvent("additionalOperatorGroup", PickerType.MultiSelect);
            adtlPopup.openPickerPopup();
            for (String additionalOperatorGroup : createEnergyCompany.getAdditionalOperatorGroups()) {
                adtlPopup.enterFilterText(additionalOperatorGroup);
                adtlPopup.clickMenuItem(additionalOperatorGroup);
            }
            adtlPopup.clickButton("OK");
        }
        
        // Added the residential groups
        if (createEnergyCompany.getResidentialGroups().size() > 0) {
            PopupMenuSolvent resPopup = PickerFactory.createPopupMenuSolvent("residentialGroup", PickerType.MultiSelect);
            resPopup.openPickerPopup();
            for (String residentialGroup : createEnergyCompany.getResidentialGroups()) {
                resPopup.enterFilterText(residentialGroup);
                resPopup.clickMenuItem(residentialGroup);
            }
            resPopup.clickButton("OK");
        }

        common.clickFormButton(createECFormAction, "save");

        Assert.assertEquals("System Administration: General Info ("+createEnergyCompany.getCompanyName()+")", common.getPageTitle());
        Assert.assertEquals(true, common.isTextPresent("Energy Company "+createEnergyCompany.getCompanyName()+" Created Successfully"));
    }

    @Override
    public void setupEnergyCompanyRoles(String yukonRoleName, Map<String, String> inputNameToRPValueMap) {
        CommonSolvent common = new CommonSolvent();
        
        common.clickFormButton(editECFormAction, "editRoles");
        roleSeleniumService.setupYukonRoleForUserOrGroup(yukonRoleName, inputNameToRPValueMap);
    }
    
    @Override
    public void deleteEnergyCompany(String energyCompanyName) {
        CommonSolvent common = new CommonSolvent();
        
        // Navigate the the energy company page
        common.clickLinkByName("System Administration");
        common.clickLinkByName("Energy Company");
        Assert.assertEquals("System Administration: Energy Companies", common.getPageTitle());
        common.clickLinkByName(energyCompanyName);
        common.clickFormButton(editECFormAction, "edit");
        
        // Delete the energy company 
        common.clickFormButton(editECFormAction, "deleteConfirmation");
        common.clickFormButton(deleteECFormAction, "delete");
        
        // Confirm the deletion of the energy company
        Assert.assertEquals("System Administration: Energy Companies", common.getPageTitle());
        Assert.assertEquals(true, common.isTextPresent("The energy company "+energyCompanyName+" was deleted successfully."));
    }
}