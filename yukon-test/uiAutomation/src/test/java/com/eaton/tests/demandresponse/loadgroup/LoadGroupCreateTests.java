package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.json.simple.JSONObject;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;
import com.eaton.rest.api.dbetoweb.DBEToWebCreateRequest;
import com.eaton.rest.api.dbetoweb.JsonFileHelper;

public class LoadGroupCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;
    private SoftAssertions softAssert;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {

        //WebDriver driver = getDriver();
        driverExt = getDriverExt();
        softAssert = new SoftAssertions();
        
        //driver.get(getBaseUrl() + Urls.DemandResponse.LOAD_GROUP_CREATE);

        //createPage = new LoadGroupCreatePage(driverExt);        
    }
    
    @BeforeMethod(alwaysRun=true)
    public void beforeTest() {
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupCreatePage(driverExt);
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE})
    public void ldGrpCreate_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Load Group";
        
        String actualPageTitle = createPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }  
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE})
    public void ldGrpCreate_NameRequiredValidation() {                
        createPage.getSaveBtn().click();

        assertThat(createPage.getName().getValidationError()).isEqualTo("Name is required.");
    }
    
    @Test
    public void ldGrpCreate_TypeRequiredValidation() {
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getType().getValidationError()).isEqualTo("Type is required.");
    }
    
    @Test
    public void ldGrpCreate_NameInvalidCharValidation() {
        
        createPage.getName().setInputValue("test/,");
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getName().getValidationError()).isEqualTo("Cannot be blank or include any of the following characters: / \\ , ' \" |");
    }
    
    @Test
    public void ldGrpCreate_CancelButtonNavigatesToCorrectUrl() {
        String expectedURL = getBaseUrl() + Urls.DemandResponse.SETUP_FILTER + "LOAD_GROUP";
        
        createPage.getCancelBtn().click();;
        String actualURL = getCurrentUrl();
        
        assertThat(actualURL).isEqualTo(expectedURL);
    }
    
    @Test
    public void ldGrpCreate_kWCapacityRequired() {

        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("kW Capacity is required.");
    }
    
    @Test
    public void ldGrpCreate_kWCapacityMaxRangeValidation() {
        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("1000000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("Must be between 0 and 99,999.999.");
    }
    
    @Test
    public void ldGrpCreate_kWCapacityMinRangeValidation() {

        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("Must be between 0 and 99,999.999.");
    }
    
    @Test
    public void ldGrpCreate_GeneralSectionTitleCorrect() {

        Section timing = createPage.getGeneralSection();
        assertThat(timing.getSection()).isNotNull();
    }
    
    @Test
    public void ldGrpCreate_OptionalAttributeSectionTitleCorrect() {
        
        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().clearInputValue();;
        Section timing = createPage.getOptionalAttributesSection();
        assertThat(timing.getSection()).isNotNull();
    }
    
    @Test
    public void ldGrpCreate_TypeDropDownContainsAllExpectedValues() {
        String expectedValues[] = {"Digi SEP Group", "ecobee Group", "asdf", "Emetcon Group", "Expresscom Group", "Honeywell Group", "Itron Group", "MCT Group", "Meter Disconnect Group", "Point Group", "RFN Expresscom Group", "Ripple Group", "Versacom Group"};
        List<String> actualValues = createPage.getType().getOptionValues();
        
        for(String exp : expectedValues) {
            softAssert.assertThat(actualValues.contains(exp)).isTrue();
        }
        softAssert.assertAll();
    }

/*    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
        refreshPage(createPage);
    }*/
}
