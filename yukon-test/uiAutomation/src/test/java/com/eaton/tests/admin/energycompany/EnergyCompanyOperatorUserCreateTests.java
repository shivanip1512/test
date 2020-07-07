package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyOperatorUserCreatePage;
import com.eaton.pages.admin.energycompany.EnergyCompanyWarehouseListPage;

public class EnergyCompanyOperatorUserCreateTests extends SeleniumTestSetup {

    private EnergyCompanyOperatorUserCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        WebDriver driver = getDriver();        
        driverExt = getDriverExt();
        
        driver.get(getBaseUrl() + Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_CREATE + "64");

        createPage = new EnergyCompanyOperatorUserCreatePage(driverExt, 64);
    }

    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.Admin.ADMIN })
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Create Operator User";
        
        String actualPageTitle = createPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS , TestConstants.Admin.ADMIN })
    public void createEnergyCompanyOperatorUserAllFieldsSuccess() {
        final String EXPECTED_USER_MSG = "Successfully created the user.";
        final String PASSWORD = "Atoperator1";
        
        Random rand = new Random();
        
        String userName = "ATOperatorUser" + rand.nextInt(1000);
        createPage.getOperatorGroup().selectItemByText("QA Admin User Grp");
        createPage.getUserName().setInputValue(userName);
        createPage.getPassword().setInputValue(PASSWORD);
        createPage.getConfirmPassword().setInputValue(PASSWORD);
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Operator Users: QA_Test", Optional.empty());
        
        EnergyCompanyWarehouseListPage listPage = new EnergyCompanyWarehouseListPage(this.driverExt);     
        
        String actualUserMsg = listPage.getUserMessage();
        
        assertThat(actualUserMsg).isEqualTo(EXPECTED_USER_MSG);
    }        
    
    @AfterMethod(alwaysRun=true)
    public void afterTest() {        
        refreshPage(createPage);
    }
}
