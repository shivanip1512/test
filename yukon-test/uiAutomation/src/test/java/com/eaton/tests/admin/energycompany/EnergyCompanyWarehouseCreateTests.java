package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyWarehouseCreatePage;
import com.eaton.pages.admin.energycompany.EnergyCompanyWarehouseListPage;

public class EnergyCompanyWarehouseCreateTests extends SeleniumTestSetup {

    private EnergyCompanyWarehouseCreatePage createPage;
    private DriverExtensions driverExt;
    private String ecId;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        ecId = TestDbDataType.EnergyCompanyData.EC_ID.getId().toString();
        navigate(Urls.Admin.ENERGY_COMPANY_WAREHOUSE_CREATE + ecId);
        createPage = new EnergyCompanyWarehouseCreatePage(driverExt, Integer.parseInt(ecId));
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void energyCompanyWarehouseCreate_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "QA_Test";
        
        String actualPageTitle = createPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void energyCompanyWarehouseCreate_AllFields_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String wareHouseName = "AT EC Warehouse " + timeStamp;
        
        final String EXPECTED_MSG = "Successfully saved " + wareHouseName + ".";
        
        createPage.getWarehouseName().setInputValue(wareHouseName);
        createPage.getAddress().setInputValue("505 State Hwy 55");
        createPage.getAddress2().setInputValue("Suite 301");
        createPage.getCity().setInputValue("Plymouth");
        createPage.getState().setInputValue("MN");
        createPage.getZip().setInputValue("55301");
        createPage.getNotes().setInputValue("Created via automated tests");
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Warehouses: QA_Test", Optional.empty());
        
        EnergyCompanyWarehouseListPage listPage = new EnergyCompanyWarehouseListPage(this.driverExt, 64);     
        
        String userMsg = listPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }       
}
