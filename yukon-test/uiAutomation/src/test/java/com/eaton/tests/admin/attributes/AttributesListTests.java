package com.eaton.tests.admin.attributes;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.attributes.AttributesListPage;

public class AttributesListTests extends SeleniumTestSetup {

    private AttributesListPage page;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
        navigate(Urls.Admin.ENERGY_COMPANY_LIST);
        page = new AttributesListPage(driverExt);
    }
    
    @AfterMethod(alwaysRun=true)
    public void afterMethod() {
        refreshPage(page);
    }
    
    @Test(groups = {TestConstants.Priority.HIGH, TestConstants.Features.ADMIN})
    public void attributeList_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Attributes";
        
        String actualPageTitle = page.getLinkedPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }    
}
