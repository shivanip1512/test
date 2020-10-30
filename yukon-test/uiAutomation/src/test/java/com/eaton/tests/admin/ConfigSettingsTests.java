package com.eaton.tests.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.ConfigSettingsPage;

public class ConfigSettingsTests extends SeleniumTestSetup {
    
    private ConfigSettingsPage page;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DriverExtensions driverExt = getDriverExt();
        navigate(Urls.Admin.CONFIGURATION);
        page = new ConfigSettingsPage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_SystemSetupSection_Displayed() {
        Section section = page.getSystemSetupSection();

        assertThat(section).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_ApplicationSection_Displayed() {
        Section section = page.getApplicationSection();

        assertThat(section).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_IntegrationSection_Displayed() {
        Section section = page.getIntegrationSection();

        assertThat(section).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.ADMIN })
    public void configSettings_OtherSection_Displayed() {
        Section section = page.getOtherSection();

        assertThat(section).isNotNull();
    }
}
