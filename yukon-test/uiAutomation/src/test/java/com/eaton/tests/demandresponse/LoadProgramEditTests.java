package com.eaton.tests.demandresponse;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupEditPage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramDetailPage;
import com.eaton.pages.demandresponse.loadprogram.LoadProgramEditPage;

public class LoadProgramEditTests extends SeleniumTestSetup {

    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun=true)
    public void beforeClass() {
        driverExt = getDriverExt();                
    }

    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void loadProgramEdit_Page_TitleCorrect() {
        final String PROGRAM_NAME = "AT Load Program";
        final String EXPECTED_TITLE = "Edit Load Program: " + PROGRAM_NAME;
        
        String ldPrgrmId = TestDbDataType.DemandResponseData.LOADPROGRAM_ID.getId().toString();
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_EDIT + ldPrgrmId + Urls.EDIT);
        
        LoadProgramEditPage editPage = new LoadProgramEditPage(driverExt, Integer.parseInt(ldPrgrmId));

        String actualPageTitle = editPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }
    
    @Test(groups = {TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void loadProgramEdit_RequiredFieldsOnly_Success() {       
        String ldPrgrmEditId = TestDbDataType.DemandResponseData.LOADPROGRAM_EDIT_ID.getId().toString();
        
        navigate(Urls.DemandResponse.LOAD_PROGRAM_EDIT + ldPrgrmEditId + Urls.EDIT);
        
        LoadGroupEditPage editPage = new LoadGroupEditPage(driverExt, Integer.parseInt(ldPrgrmEditId));
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Edited Direct Program " + timeStamp;
        final String EXPECTED_MSG = name + " saved successfully.";
        
        editPage.getName().setInputValue(name);
        
        editPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Program: " + name, Optional.empty());
        
        LoadProgramDetailPage detailsPage = new LoadProgramDetailPage(driverExt, Integer.parseInt(ldPrgrmEditId));
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }       
}
