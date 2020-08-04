package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupEmetconCreatePage;

public class LoadGroupEmetconCreateTests extends SeleniumTestSetup{

    private LoadGroupEmetconCreatePage createPage;
    private DriverExtensions driverExt;
    private Random randomNum;
    
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        driverExt = getDriverExt();
        new SoftAssertions();
        randomNum = getRandomNum();
        
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupEmetconCreatePage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
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
        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
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
    public void ldGrpCreateEmetcon_goldAddress_MaxRangeValidation() {
        String expectedErrorMsg = "Must be between 0 and 4.";
        
        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().setInputValue("5");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getGoldAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_goldAddress_MinRangeValidation() {
        String expectedErrorMsg = "Must be between 0 and 4.";
        
        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().setInputValue("-1");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getGoldAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_goldAddress_RequiredValidation() {
        String expectedErrorMsg = "Gold Address is required.";
        
        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
        waitForLoadingSpinner();
        
        createPage.getGoldAddress().clearInputValue();
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getGoldAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_silverAddress_RequiredValidation() {
        String expectedErrorMsg = "Silver Address is required.";
        
        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
        waitForLoadingSpinner();
        
        createPage.getSilverAddress().clearInputValue();
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getSilverAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_silverAddress_MaxRangeValidation() {
        String expectedErrorMsg = "Must be between 0 and 60.";
        
        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
        waitForLoadingSpinner();
        
        createPage.getSilverAddress().setInputValue("61");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getSilverAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_silverAddress_MinRangeValidation() {
        String expectedErrorMsg = "Must be between 0 and 60.";
        
        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
        waitForLoadingSpinner();
        
        createPage.getSilverAddress().setInputValue("-1");
        
        createPage.getSaveBtn().click();
        
        assertThat(createPage.getSilverAddress().getValidationError()).isEqualTo(expectedErrorMsg);
    } 
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_AddressingSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
        waitForLoadingSpinner();  
        
        Section general = createPage.getPageSection("Addressing");
        
        assertThat(general.getSection()).isNotNull();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_CommunicationRoute_LabelsCorrect() {
        String sectionName = "General";
        String expectedLabel = "Communication Route:";

        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
        waitForLoadingSpinner();
        
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels.contains(expectedLabel)).isTrue();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void ldGrpCreateEmetcon_AddressingSection_LabelsCorrect() {
        String sectionName = "Addressing";
        List<String> expectedLabels = new ArrayList<>(List.of("Gold Address:", "Silver Address:", "Address To Use:", "Relay To Use:"));

        createPage.getType().selectItemByValue("LM_GROUP_EMETCON");
        waitForLoadingSpinner();
        
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();
        
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
}
