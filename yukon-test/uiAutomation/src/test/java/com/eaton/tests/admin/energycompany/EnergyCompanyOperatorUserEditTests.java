package com.eaton.tests.admin.energycompany;

import static org.assertj.core.api.Assertions.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.modals.ConfirmModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.admin.energycompany.EnergyCompanyOperatorUserEditPage;
import com.eaton.pages.admin.energycompany.EnergyCompanyOperatorUserListPage;

public class EnergyCompanyOperatorUserEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;
    private String ecId;
    
    
    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();   
        ecId = TestDbDataType.EnergyCompanyData.EC_ID.getId().toString();
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void energyCompanyOperatorUserEdit_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Edit Operator User: automation";
        String ecOperatorId = TestDbDataType.EnergyCompanyData.EC_AUTOMATION_OPR_ID.getId().toString();
        
        navigate(Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_EDIT + ecId + Urls.Admin.ENERGY_COMPANY_OPERATOR_LOGIN_ID + ecOperatorId);
        
        EnergyCompanyOperatorUserEditPage page = new EnergyCompanyOperatorUserEditPage(driverExt, Integer.parseInt(ecId), Integer.parseInt(ecOperatorId));
                                 
        String actualPageTitle = page.getPageTitle();
        
        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    } 

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.ADMIN})
    public void energyCompanyOperatorUserEdit_Delete_Success() {
        final String EXPECTED_USER_MSG = "Successfully deleted the user.";
        String ecDeleteOprId = TestDbDataType.EnergyCompanyData.EC_DELETE_OPR_ID.getId().toString();
        
        navigate(Urls.Admin.ENERGY_COMPANY_OPERATOR_USER_EDIT + ecId + Urls.Admin.ENERGY_COMPANY_OPERATOR_LOGIN_ID + ecDeleteOprId);

        EnergyCompanyOperatorUserEditPage editPage = new EnergyCompanyOperatorUserEditPage(driverExt, Integer.parseInt(ecId), Integer.parseInt(ecDeleteOprId));

        ConfirmModal modal = editPage.showAndWaitConfirmDeleteModal();
        
        modal.clickOkAndWaitForModalToClose();

        EnergyCompanyOperatorUserListPage listPage = new EnergyCompanyOperatorUserListPage(driverExt, Integer.parseInt(ecId));

        String actualUserMsg = listPage.getUserMessage();

        assertThat(EXPECTED_USER_MSG).isEqualTo(actualUserMsg);
    }  
}
