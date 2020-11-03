package com.eaton.pages.admin;

import java.util.Optional;

import org.openqa.selenium.By;

import com.eaton.elements.ActionBtnDropDownElement;
import com.eaton.elements.IconLinkButton;
import com.eaton.elements.Section;
import com.eaton.elements.modals.TestEmailModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.pages.PageBase;

public class ConfigSettingsPage extends PageBase {
    
    public ConfigSettingsPage (DriverExtensions driverExt, String pageUrl) {
        super(driverExt);

        requiresLogin = true;
        this.pageUrl = pageUrl;
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
        return new ActionBtnDropDownElement(driverExt);
    }
    
    public Section getSystemSetupSection() {
        return new Section(driverExt, "System Setup");
    }
    
    public Section getApplicationSection() {
        return new Section(driverExt, "Application"); 
    }
    
    public Section getIntegrationSection() {
        return new Section(driverExt, "Integration");
    }
    
    public Section getOtherSection() {
        return new Section(driverExt, "Other");
    }
    
    public IconLinkButton getAttributesBtn() {
        return new IconLinkButton(driverExt, "attributes", getSystemSetupSection().getSection());
    }
    
    public IconLinkButton getAuthenticationBtn() {
        return new IconLinkButton(driverExt, "authentication", getSystemSetupSection().getSection());
    }
    
    public IconLinkButton getDashboardAdminBtn() {
        return new IconLinkButton(driverExt, "dashboard_admin", getSystemSetupSection().getSection());
    }
    
    public IconLinkButton getDemandResponseBtn() {
        return new IconLinkButton(driverExt, "dr", getSystemSetupSection().getSection());
    }
    
    public IconLinkButton getEndpointBtn() {
        return new IconLinkButton(driverExt, "ami", getSystemSetupSection().getSection());
    }
    
    public IconLinkButton getThemesBtn() {
        return new IconLinkButton(driverExt, "themes", getSystemSetupSection().getSection());
    }
    
    public IconLinkButton getWebServerBtn() {
        return new IconLinkButton(driverExt, "web_server", getSystemSetupSection().getSection());
    }
    
    public IconLinkButton getYukonServicesBtn() {
        return new IconLinkButton(driverExt, "yukon_services", getSystemSetupSection().getSection());
    }
    
    public IconLinkButton getDashboardWidgetsBtn() {
        return new IconLinkButton(driverExt, "dashboard_widget", getApplicationSection().getSection());
    }
    
    public IconLinkButton getDataImportExportBtn() {
        return new IconLinkButton(driverExt, "data_import_export", getApplicationSection().getSection());
    }
    
    public IconLinkButton getTrendsBtn() {
        return new IconLinkButton(driverExt, "graphing", getApplicationSection().getSection());
    }
    
    public IconLinkButton getMultispeakBtn() {
        return new IconLinkButton(driverExt, "multispeak", getIntegrationSection().getSection());
    }
    
    public IconLinkButton getOpenAdrBtn() {
        return new IconLinkButton(driverExt, "open_adr", getIntegrationSection().getSection());
    }
    
    public IconLinkButton getVoiceBtn() {
        return new IconLinkButton(driverExt, "voice", getIntegrationSection().getSection());
    }
    
    public IconLinkButton getWeatherBtn() {
        return new IconLinkButton(driverExt, "weather", getIntegrationSection().getSection());
    }
    
    public IconLinkButton getMiscBtn() {
        return new IconLinkButton(driverExt, "misc", getOtherSection().getSection());
    }
    
    public IconLinkButton getSecurityBtn() {
        return new IconLinkButton(driverExt, "security", getOtherSection().getSection());
    }
    
    public TestEmailModal showTestEmailModal() {
        getActionBtn().clickAndSelectOptionByText("Test Email");

        SeleniumTestSetup.waitUntilModalOpenByDescribedBy("adminSetup-testEmail-popup");

        return new TestEmailModal(this.driverExt, Optional.empty(), Optional.of("adminSetup-testEmail-popup"));
    }
}
