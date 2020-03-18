package com.eaton.tests.admin.energycompany;

import java.util.Optional;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.eaton.elements.WebTable;
import com.eaton.elements.WebTableRow;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyOperatorUserEditPage;
import com.eaton.pages.admin.energycompany.EnergyCompanyOperatorUserListPage;

public class EnergyCompanyOperatorUserEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private SoftAssert softAssertion;
    
    @BeforeClass
    public void beforeClass() {
        driverExt = getDriverExt();   
        softAssertion = getSoftAssertion();
    }
    
    @Test(groups = {TestConstants.TestNgGroups.SMOKE_TESTS, ""})
    public void pageTitleCorrect() {
        final String EXPECTED_TITLE = "Edit Operator User: automation";
        
        navigate(Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_EDIT + "64" + Urls.Admin.ENERGY_COMPANY_OPERATOR_LOGIN_ID + "212");
        
        EnergyCompanyOperatorUserEditPage page = new EnergyCompanyOperatorUserEditPage(driverExt, Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_EDIT + "64" + Urls.Admin.ENERGY_COMPANY_OPERATOR_LOGIN_ID + "212");
                                 
        String actualPageTitle = page.getPageTitle();
        
        Assert.assertEquals(actualPageTitle, EXPECTED_TITLE, "Expected Page title: '" + EXPECTED_TITLE + "' but found: " + actualPageTitle);
    } 

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, "SM03_07_editRFNOjects" })
    public void deleteOperatorUserSuccess() {
        final String EXPECTED_USER_MSG = "Successfully deleted the user";
        final String OPERATOR_USER = "ATDeleteUser";
        
        navigate(Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_EDIT + "64" + Urls.Admin.ENERGY_COMPANY_OPERATOR_LOGIN_ID + "302");

        EnergyCompanyOperatorUserEditPage editPage = new EnergyCompanyOperatorUserEditPage(driverExt, Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_EDIT + "64" + Urls.Admin.ENERGY_COMPANY_OPERATOR_LOGIN_ID + "302");

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWait();

        waitForUrlToLoad(Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_LIST + "64", Optional.empty());

        EnergyCompanyOperatorUserListPage listPage = new EnergyCompanyOperatorUserListPage(driverExt, Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_LIST + "64");

        String actualUserMsg = listPage.getUserMessage();
        WebTable table = listPage.getTable();
        WebTableRow row = table.getDataRowByName(OPERATOR_USER);

        softAssertion.assertEquals(actualUserMsg, EXPECTED_USER_MSG, "Expected User Msg: '" + EXPECTED_USER_MSG + "' but found " + actualUserMsg);
        softAssertion.assertNotNull(row);
    }  
}
