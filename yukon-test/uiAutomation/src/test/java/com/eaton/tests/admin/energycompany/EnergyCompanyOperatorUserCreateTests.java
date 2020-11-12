package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;
import java.util.Random;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyOperatorUserCreatePage;
import com.eaton.pages.admin.energycompany.EnergyCompanyWarehouseListPage;

public class EnergyCompanyOperatorUserCreateTests extends SeleniumTestSetup {

    private EnergyCompanyOperatorUserCreatePage createPage;
    private DriverExtensions driverExt;
    private String ecId;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
        
        setRefreshPage(false);
        ecId = TestDbDataType.EnergyCompanyData.EC_ID.getId().toString();
        navigate(Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_CREATE + ecId);
        createPage = new EnergyCompanyOperatorUserCreatePage(driverExt, Integer.parseInt(ecId));
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void energyCompanyOperatorUserCreate_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Operator User";
        
        String actualPageTitle = createPage.getPageTitle();
        
        assertThat(EXPECTED_TITLE).isEqualTo(actualPageTitle);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void energyCompanyOperatorUserCreate_AllFields_Success() {
        setRefreshPage(true);
        final String EXPECTED_USER_MSG = "Successfully created the user.";
        final String PASSWORD = "Atoperator1";
        
        Random rand = new Random();
        
        String userName = "ATOperatorUser" + rand.nextInt(1000);
        createPage.getOperatorGroup().selectItemByValue("QA Admin User Grp");
        createPage.getUserName().setInputValue(userName);
        createPage.getPassword().setInputValue(PASSWORD);
        createPage.getConfirmPassword().setInputValue(PASSWORD);
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Operator Users: QA_Test", Optional.empty());
        
        EnergyCompanyWarehouseListPage listPage = new EnergyCompanyWarehouseListPage(this.driverExt);     
        
        String actualUserMsg = listPage.getUserMessage();
        
        assertThat(EXPECTED_USER_MSG).isEqualTo(actualUserMsg);
    }            
}
