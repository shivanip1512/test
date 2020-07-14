package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;

public class LoadGroupEmetconCreateTests extends SeleniumTestSetup{

    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;
    private Random randomNum;
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        driverExt = getDriverExt();
        new SoftAssertions();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupCreatePage(driverExt);
        randomNum = getRandomNum();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_createAllFieldsSuccess() {
        
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT emetcon " + timeStamp;
        double randomDouble = randomNum.nextDouble();   
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;
        
        final String EXPECTED_MSG = name + " saved successfully.";
        
        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByText("Emetcon Group");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().setInputValue("2");
        createPage.getSilverAddress().setInputValue("3");
        
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
        
        createPage.getSaveBtn().click();
        
        waitForPageToLoad("Load Group: " + name, Optional.empty());
        
        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        
        String userMsg = detailsPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }  
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_goldAddressMaxRange() {
        String expectedErrorMsg = "Must be between 0 and 4.";
        
        createPage.getName().setInputValue("TestEmetcon");
        createPage.getType().selectItemByText("Emetcon Group");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().setInputValue("5");
        createPage.getSilverAddress().setInputValue("3");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getGoldAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_goldAddressMinRange() {
        String expectedErrorMsg = "Must be between 0 and 4.";
        
        createPage.getName().setInputValue("TestEmetcon");
        createPage.getType().selectItemByText("Emetcon Group");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().setInputValue("-1");
        createPage.getSilverAddress().setInputValue("3");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getGoldAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_goldAddressRequired() {
        String expectedErrorMsg = "Gold Address is required.";
        
        createPage.getName().setInputValue("TestEmetcon");
        createPage.getType().selectItemByText("Emetcon Group");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().setInputValue("");
        createPage.getSilverAddress().setInputValue("3");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getGoldAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_silverAddressRequired() {
        String expectedErrorMsg = "Silver Address is required.";
        
        createPage.getName().setInputValue("TestEmetcon");
        createPage.getType().selectItemByText("Emetcon Group");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().setInputValue("3");
        createPage.getSilverAddress().setInputValue("");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getSilverAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_silverAddressMaxRange() {
        String expectedErrorMsg = "Must be between 0 and 60.";
        
        createPage.getName().setInputValue("TestEmetcon");
        createPage.getType().selectItemByText("Emetcon Group");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().setInputValue("2");
        createPage.getSilverAddress().setInputValue("61");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getSilverAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_silverAddressMinRange() {
        String expectedErrorMsg = "Must be between 0 and 60.";
        
        createPage.getName().setInputValue("TestEmetcon");
        createPage.getType().selectItemByText("Emetcon Group");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().setInputValue("3");
        createPage.getSilverAddress().setInputValue("-1");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getSilverAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_AddressingSectionTitleCorrect() {

        createPage.getType().selectItemByText("Emetcon Group");
        createPage.getkWCapacity().setInputValue("2");
        Section general = createPage.getSection("Addressing");
        assertThat(general.getSection()).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_CommunicationRouteLabelsCorrect() {
        String sectionName = "General";
        String expectedLabel = "Communication Route:";

        createPage.getType().selectItemByText("Emetcon Group");
        createPage.getkWCapacity().setInputValue("22");
        List<String> actualLabels = createPage.getSection(sectionName).getSectionLabels();

        assertThat(actualLabels.contains(expectedLabel)).isTrue();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_AddressingSectionLabelsCorrect() {
        String sectionName = "Addressing";
        List<String> expectedLabels = new ArrayList<>(List.of("Gold Address:", "Silver Address:", "Address To Use:", "Relay To Use:"));

        createPage.getType().selectItemByText("Emetcon Group");
        createPage.getkWCapacity().setInputValue("2");
        List<String> actualLabels = createPage.getSection(sectionName).getSectionLabels();
        
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
}
