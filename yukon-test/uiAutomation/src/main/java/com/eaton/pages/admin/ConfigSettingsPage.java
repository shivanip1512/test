package com.eaton.pages.admin;

import org.openqa.selenium.By;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.IconLinkButton;
import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.pages.PageBase;

public class ConfigSettingsPage extends PageBase {
    
    private ActionBtnDropDownElement actions;
    private Section systemSetupSection;
    private Section applicationSection;
    private Section integrationSection;
    private Section otherSection;
    private IconLinkButton attributesBtn;

    public ConfigSettingsPage(DriverExtensions driverExt) {
        super(driverExt);
        
        actions = new ActionBtnDropDownElement(driverExt);
        systemSetupSection = new Section(driverExt, "System Setup");
        applicationSection = new Section(driverExt, "Application");        
        integrationSection = new Section(driverExt, "Integration");
        otherSection = new Section(driverExt, "Other");
        attributesBtn = new IconLinkButton(driverExt, "attributes", getSystemSetupSection().getSection());
    }
    
    public Integer getSystemSetupBtnCount() {
        return getSystemSetupSection().getSection().findElements(By.cssSelector("button")).size();
    }
    
    public Integer getApplicationBtnCount() {
        return getApplicationSection().getSection().findElements(By.cssSelector("button")).size();
    }
    
    public Integer getIntegrationBtnCount() {
        return getIntegrationSection().getSection().findElements(By.cssSelector("button")).size();
    }
    
    public Integer getOtherBtnCount() {
        return getOtherSection().getSection().findElements(By.cssSelector("button")).size();
    }
    
    public ActionBtnDropDownElement getActionBtn() {
        return actions;
    }
    
    public Section getSystemSetupSection() {
        return systemSetupSection;
    }
    
    public Section getApplicationSection() {
        return applicationSection;
    }
    
    public Section getIntegrationSection() {
        return integrationSection;
    }
    
    public Section getOtherSection() {
        return otherSection;
    }
    
    public IconLinkButton getAttributesBtn() {
        return attributesBtn;
    }
}
