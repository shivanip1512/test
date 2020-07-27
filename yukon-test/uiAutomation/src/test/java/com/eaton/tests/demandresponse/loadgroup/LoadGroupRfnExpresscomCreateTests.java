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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupRFNExpressCommCreatePage;

	public class LoadGroupRfnExpresscomCreateTests extends SeleniumTestSetup{

	    private LoadGroupRFNExpressCommCreatePage createPage;
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
	        createPage = new LoadGroupRFNExpressCommCreatePage(driverExt);
	        randomNum = getRandomNum();
	    }
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_createAllFieldsSuccess() {
	    	String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
	        String name = "RFN Expresscomm" + timeStamp;
	        double randomDouble = randomNum.nextDouble();
	        int randomInt = randomNum.nextInt(9999);
	        double capacity = randomDouble + randomInt;

	        final String EXPECTED_MSG = name + " saved successfully.";

	        createPage.getName().setInputValue(name);
	        createPage.getType().selectItemByText("RFN Expresscom Group");

	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("GEO", true);
	        createPage.getAddressUsage().setTrueFalseByName("Substation", true);
	        createPage.getAddressUsage().setTrueFalseByName("Feeder", true);
	        createPage.getAddressUsage().setTrueFalseByName("Zip", true);
	        createPage.getAddressUsage().setTrueFalseByName("User", true);
	        
	        createPage.getGeoAddress().setInputValue("10");
	        createPage.getSubstationAddress().setInputValue("10");
	        createPage.getZipAddress().setInputValue("5");
	        createPage.getUserAddress().setInputValue("6");
	        
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getProgramLoadAddress().setInputValue("10");
	        
	        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

	        createPage.getSaveBtn().click();

	        waitForPageToLoad("Load Group: " + name, Optional.empty());

	        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
	        String userMsg = detailsPage.getUserMessage();
	        
	        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
	    }


	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_InvalidValueErrorGeographicalAddressingForGEO() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("GEO", true);
	        createPage.getGeoAddress().setInputValue("aaa");
	        createPage.getSaveBtn().click();
	        String errorMsg = createPage.getFieldValidationError("geo").getValidationError();
	        final String exptd_msg_geo= "Must be a valid integer value.";
	        assertThat(errorMsg).isEqualTo(exptd_msg_geo);
	    	
	    }
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_InvalidValueErrorGeographicalAddressingForSubstation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("Substation", true);
	        createPage.getSubstationAddress().setInputValue("aaa");
	        createPage.getSaveBtn().click();
	        String errorMsg = createPage.getFieldValidationError("substation").getValidationError();
	        final String exptd_msg_substation= "Must be a valid integer value.";
	        assertThat(errorMsg).isEqualTo(exptd_msg_substation);
	    	
	    } 
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_ValidationErrorOnKeepingZipValueBlank() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("Zip", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getProgramLoadAddress().setInputValue("10");
	        createPage.getSaveBtn().click();
	        String errorMsg = createPage.getFieldValidationError("zip").getValidationError();
	        final String exptd_msg_zip= "Zip is required.";
	        assertThat(errorMsg).isEqualTo(exptd_msg_zip);
	    	
	    } 
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_ValidationErrorOnKeepingUserValueBlank() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("User", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getProgramLoadAddress().setInputValue("10");
	        createPage.getSaveBtn().click();
	        String errorMsg = createPage.getFieldValidationError("user").getValidationError();
	        final String exptd_msg_user= "User is required.";
	        assertThat(errorMsg).isEqualTo(exptd_msg_user);
	    	
	    } 
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_ValidationErrorOnKeepingSerialNumberBlank() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("Serial", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getProgramLoadAddress().setInputValue("10");
	        createPage.getSaveBtn().click();
	        String errorMsg = createPage.getFieldValidationError("serialNumber").getValidationError();
	        final String exptd_msg_serial= "Serial Number is required.";
	        assertThat(errorMsg).isEqualTo(exptd_msg_serial);
	    	
	    } 
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeneralSectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        Section generalSection = createPage.getPageSection("General");
	        
	        assertThat(generalSection.getSection()).isNotNull();
	    }

	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeneralSectionLabelsCorrect() {
	        String sectionName = "General";
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:"));
	        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

	        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressSectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        Section generalSection = createPage.getPageSection("Geographical Address");
	        
	        assertThat(generalSection.getSection()).isNotNull();
	    }

	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressSectionLabelsCorrect() {
	        String sectionName = "Geographical Address";
	        String expectedLabels = "Address Usage:";
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();

	        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);
	        
	        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
	                .isTrue();
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingSectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        Section generalSection = createPage.getPageSection("Geographical Addressing");
	        
	        assertThat(generalSection.getSection()).isNotNull();
	    }

	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingSectionLabelsCorrect() {
	      String sectionName = "Geographical Addressing";
	      createPage.getType().selectItemByText("RFN Expresscom Group");
	      waitForLoadingSpinner();
	    createPage.getAddressUsage().setTrueFalseByName("GEO", true);
        createPage.getAddressUsage().setTrueFalseByName("Substation", true);
        createPage.getAddressUsage().setTrueFalseByName("Feeder", true);
        createPage.getAddressUsage().setTrueFalseByName("Zip", true);
        createPage.getAddressUsage().setTrueFalseByName("User", true);
        createPage.getAddressUsage().setTrueFalseByName("Serial", true);
        
	        List<String> expectedLabels = new ArrayList<>(List.of("SPID:", "GEO:", "Substation:", "Feeder:", "ZIP:", "User:", "Serial:"));
	        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

	        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressSectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        Section generalSection = createPage.getPageSection("Load Address");
	        
	        assertThat(generalSection.getSection()).isNotNull();
	    }

	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressSectionLabelsCorrect() {
	        String sectionName = "Load Address";
	        String expectedLabels = "Usage:";
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();

	        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);
	        
	        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
	                .isTrue();
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressingSectionLabelsCorrect() {
	        String sectionName = "Load Addressing";
	        createPage.getType().selectItemByText("RFN Expresscom Group");

	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Splinter", true);
	        
	        List<String> expectedLabels = new ArrayList<>(
	                List.of("Send Loads in Control Message:", "Loads:", "Program:", "Splinter:"));
	        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

	        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressingSectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();

	        Section generalSection = createPage.getPageSection("Load Addressing");
	        assertThat(generalSection.getSection()).isNotNull();
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_OptionalAttributesSectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();

	        Section generalSection = createPage.getPageSection("Optional Attributes");
	        assertThat(generalSection.getSection()).isNotNull();
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_OptionalAttributesSectionLabelsCorrect() {
	        String sectionName = "Optional Attributes";
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();

	        List<String> expectedLabels = new ArrayList<>(List.of("Control Priority:", "kW Capacity:", "Disable Group:", "Disable Control:"));
	        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

	        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressingProgramMinRangeValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getProgramLoadAddress().setInputValue("0");
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getProgramLoadAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressingProgramMaxRangeValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getProgramLoadAddress().setInputValue("255");
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getProgramLoadAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressingSplinterMinRangeValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Splinter", true);
	        createPage.getSplinterLoadAddress().setInputValue("0");
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getSplinterLoadAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressingSplinterMaxRangeValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Splinter", true);
	        createPage.getSplinterLoadAddress().setInputValue("255");
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getSplinterLoadAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
	    }

	    
	    @AfterMethod(alwaysRun = true)
	    public void afterTest() {
	        refreshPage(createPage);
	        createPage = new LoadGroupRFNExpressCommCreatePage(driverExt);
	    }
	    
	}