package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupCreatePage;

public class LoadGroupHoneywellCreateTests extends SeleniumTestSetup{
	private LoadGroupCreatePage createPage;
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
        createPage = new LoadGroupCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void loadGroupCreateHoneywell_RequiredFieldsOnlySuccessfully() {
    	 String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());        
         String name = "Honeywell Group " + timeStamp;
         double randomDouble = randomNum.nextDouble();
         int randomInt = randomNum.nextInt(9999);
         double capacity = randomDouble + randomInt;       

         final String EXPECTED_MSG = name + " saved successfully.";
         
         createPage.getType().selectItemByText("Honeywell Group");

         waitForLoadingSpinner();
         createPage.getName().setInputValue(name);      
         createPage.getkWCapacity().setInputValue(String.valueOf(capacity));
         createPage.getSaveBtn().click();

         waitForPageToLoad("Load Group: " + name, Optional.empty());

         LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
         String userMsg = detailsPage.getUserMessage();
         
         assertThat(userMsg).isEqualTo(EXPECTED_MSG);

    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void loadGroupCreateHoneywell_DisableGroupAndControlOnlySuccessfully() {
    	 String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());        
         String name = "Honeywell Group " + timeStamp;
         double randomDouble = randomNum.nextDouble();
         int randomInt = randomNum.nextInt(9999);
         double capacity = randomDouble + randomInt;       

         final String EXPECTED_MSG = name + " saved successfully.";
         
         createPage.getType().selectItemByText("Honeywell Group");

         waitForLoadingSpinner();
         createPage.getName().setInputValue(name);      
         createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

         createPage.getDisableGroup().setValue(true);
         createPage.getDisableControl().setValue(true);

         createPage.getSaveBtn().click();

         waitForPageToLoad("Load Group: " + name, Optional.empty());

         LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
         String userMsg = detailsPage.getUserMessage();
         
         assertThat(userMsg).isEqualTo(EXPECTED_MSG);

    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void loadGroupCreateHoneywell_KWCapacityMinValueValidation() {
        createPage.getType().selectItemByText("Honeywell Group");
        waitForLoadingSpinner();

        createPage.getkWCapacity().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("Must be between 0 and 99,999.999.");
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void loadGroupCreateHoneywell_KWCapacityMaxValueValidation() {
        createPage.getType().selectItemByText("Honeywell Group");
        waitForLoadingSpinner();

        createPage.getkWCapacity().setInputValue("100000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("Must be between 0 and 99,999.999.");
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void loadGroupCreateHoneywell_GeneralSectionTitleCorrect() {
        createPage.getType().selectItemByText("Honeywell Group");
        waitForLoadingSpinner();
        Section generalSection = createPage.getPageSection("General");
        
        assertThat(generalSection.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
    public void loadGroupCreateHoneywell_GeneralSectionLabelsCorrect() {
        String sectionName = "General";
        createPage.getType().selectItemByText("Honeywell Group");
        waitForLoadingSpinner();
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:"));
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

    }
    
    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
        createPage = new LoadGroupCreatePage(driverExt);
    }
	

}
