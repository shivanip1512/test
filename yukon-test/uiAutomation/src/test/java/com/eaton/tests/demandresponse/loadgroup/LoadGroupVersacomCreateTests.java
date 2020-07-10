package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;

public class LoadGroupVersacomCreateTests extends SeleniumTestSetup{
    
    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;
    private SoftAssertions softly;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        WebDriver driver = getDriver();
        driverExt = getDriverExt();
}

    @BeforeMethod(alwaysRun=true)
    public void beforeTest() {
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupCreatePage(driverExt);
        createPage.getType().selectItemByText("Versacom Group");
    }
    
    @Test(groups = {TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE})
    public void ldGrpCreate_NameRequiredValidation() {                
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getName().getValidationError()).isEqualTo("Name is required.");
    }
    
    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.COMM_CHANNEL })
    public void commChannelDetailsTcp_InfoTabLabelsCorrect() {
        String sectionName = "General";
        String sectionNames = "Optional Attributes";
        
        List<String> labels = createPage.getGeneralSection().getSectionLabels(sectionName);
        List<String> labels1 = createPage.getOptionalAttributesSection().getSectionLabels(sectionNames);
        

    }
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
        refreshPage(createPage);
    }
}
