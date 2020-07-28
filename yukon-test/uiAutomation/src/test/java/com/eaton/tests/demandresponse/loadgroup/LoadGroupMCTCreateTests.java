package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import com.eaton.elements.Section;
import com.eaton.elements.modals.SelectMCTMeterModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

import com.eaton.pages.demandresponse.LoadGroupMCTCreatePage;

public class LoadGroupMCTCreateTests extends SeleniumTestSetup {

    private LoadGroupMCTCreatePage createPage;
    WebDriver driver;
    private DriverExtensions driverExt;
    private Random randomNum;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        randomNum = getRandomNum();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupMCTCreatePage(driverExt);
    }
    
/*	@Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
	public void ldGrpCreateMCT_AllFieldsSuccessfully() {
    String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
    String name = "AT MCT " + timeStamp;
	double randomDouble = randomNum.nextDouble();
	int randomInt = randomNum.nextInt(9999);
	double capacity = randomDouble + randomInt;
	
	final String EXPECTED_MSG = name + " saved successfully.";
	
	createPage.getType().selectItemByText("MCT Group");
	
	waitForLoadingSpinner();
	createPage.getName().setInputValue(name);
	createPage.getAddress().setInputValue("2");
	createPage.getRelayMCT().setTrueFalseByName("Relay 2", true);
	
	createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
	createPage.getDisableGroup().setValue(true);
	createPage.getDisableControl().setValue(false);
	createPage.getSaveBtn().click();
	
	waitForPageToLoad("Load Group: " + name, Optional.empty());
	
    LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
    String userMsg = detailsPage.getUserMessage();

    assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    
	}

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateMCT_CommunicationRouteLabelsCorrect() {
        String sectionName = "General";
        String expectedLabel = "Communication Route:";

        createPage.getType().selectItemByText("MCT Group");
        waitForLoadingSpinner();
        
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels.contains(expectedLabel)).isTrue();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateMCT_AddressingSectionLabelsCorrect() {
        String sectionName = "Addressing";
        List<String> expectedLabels = new ArrayList<>(List.of("Address Level:", "Address:", "Relay Usage:"));

        createPage.getType().selectItemByText("MCT Group");
        waitForLoadingSpinner();
        
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();
        
        assertThat(actualLabels).containsAll(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateMCT_AddressingSectionTitleCorrect() {

        createPage.getType().selectItemByText("MCT Group");
        waitForLoadingSpinner();
        
        Section address = createPage.getPageSection("Addressing");
        assertThat(address.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateMCT_AddressMinRange() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT MCT " + timeStamp;
        String expectedErrorMsg = "Must be between 1 and 2,147,483,647.";
        
        createPage.getType().selectItemByText("MCT Group");
        waitForLoadingSpinner();
        
        createPage.getName().setInputValue(name);
        createPage.getAddress().setInputValue("0");

        createPage.getSaveBtn().click();      
        assertThat(createPage.getAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateMCT_AddressMaxRange() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT MCT " + timeStamp;
        String expectedErrorMsg = "Must be between 1 and 2,147,483,647.";
        
        createPage.getType().selectItemByText("MCT Group");
        waitForLoadingSpinner();
        
        createPage.getName().setInputValue(name);
        createPage.getAddress().setInputValue("2147483648");

        createPage.getSaveBtn().click();       
        assertThat(createPage.getAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateMCT_AddressRequired() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT MCT " + timeStamp;
        String expectedErrorMsg = "Address is required.";
        
        createPage.getType().selectItemByText("MCT Group");
        waitForLoadingSpinner();
        
        createPage.getName().setInputValue(name);
        createPage.getAddress().setInputValue(" ");

        createPage.getSaveBtn().click();
        assertThat(createPage.getAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    }*/
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateMCT_MCTAddressSelection() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT MCT " + timeStamp;
        
        createPage.getType().selectItemByText("MCT Group");
        waitForLoadingSpinner();
        
        createPage.getName().setInputValue(name);      
        createPage.getAddressLevel().selectItemByText("MCT Address");
        
        SelectMCTMeterModal mctMeterModal = this.createPage.showAndWaitMCTMeter();
        mctMeterModal.selectMeter("a_MCT-430A");
        mctMeterModal.clickOkAndWait();
        createPage.getSaveBtn().click();
    }   
}
