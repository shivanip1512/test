package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyDeleteConfirmationPage;
import com.eaton.pages.admin.energycompany.EnergyCompanyGeneralInfoEditPage;
import com.eaton.pages.admin.energycompany.EnergyCompanyGeneralInfoPage;
import com.eaton.pages.admin.energycompany.EnergyCompanyListPage;

public class EnergyCompanyGeneralInfoEditTests extends SeleniumTestSetup {
    
    private DriverExtensions driverExt;
    
    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void energyCompanyGeneralInfoEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit General Info: QA_Test";
        
        navigate(Urls.Admin.ENERGY_COMPANY_GENERAL_INFO + "64");
        
        EnergyCompanyGeneralInfoPage page = new EnergyCompanyGeneralInfoPage(this.driverExt, 64);
        
        page.getEditBtn().click();
        
        waitForUrlToLoad(Urls.Admin.ENERGY_COMPANY_EDIT, Optional.empty());
        
        EnergyCompanyGeneralInfoEditPage editPage = new EnergyCompanyGeneralInfoEditPage(this.driverExt, 64);
        
        String actualPageTitle = editPage.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Admin.ADMIN})
    public void energyCompanyGeneralInfoEdit_Delete_Success() {
        final String NAME = "AT Delete EC";
        final String EXPECTED_MSG = "The energy company " + NAME + " was deleted successfully.";
        
        navigate(Urls.Admin.ENERGY_COMPANY_GENERAL_INFO + "822");
        
        EnergyCompanyGeneralInfoPage page = new EnergyCompanyGeneralInfoPage(this.driverExt, 822);
        
        page.getEditBtn().click();
        
        waitForUrlToLoad(Urls.Admin.ENERGY_COMPANY_EDIT, Optional.empty());
        
        EnergyCompanyGeneralInfoEditPage editPage = new EnergyCompanyGeneralInfoEditPage(this.driverExt, 822);
        
        editPage.getDeleteBtn().click();
        
        waitForPageToLoad("deleteEnergyCompanyConfirm: " + NAME, Optional.empty()); 
        
        EnergyCompanyDeleteConfirmationPage  deletePage = new EnergyCompanyDeleteConfirmationPage(driverExt, 822);
        
        deletePage.getDeleteBtn().click();
        
        waitForPageToLoad("Energy Companies", Optional.empty());
        
        EnergyCompanyListPage listPage = new EnergyCompanyListPage(driverExt);
        
        String userMsg = listPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }   
}
