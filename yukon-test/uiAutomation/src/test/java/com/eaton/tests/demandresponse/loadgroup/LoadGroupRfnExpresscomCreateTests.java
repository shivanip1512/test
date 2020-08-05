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
import com.eaton.pages.demandresponse.LoadGroupRfnExpressCommCreatePage;

	public class LoadGroupRfnExpresscomCreateTests extends SeleniumTestSetup{

	    private LoadGroupRfnExpressCommCreatePage createPage;
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
	        createPage = new LoadGroupRfnExpressCommCreatePage(driverExt);
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
	        createPage.getAddressUsage().setTrueFalseByName("ZIP", true);
	        createPage.getAddressUsage().setTrueFalseByName("User", true);
	        
	        createPage.getGeoAddress().setInputValue("10");
	        createPage.getSubstationAddress().setInputValue("10");
	        createPage.getZipAddress().setInputValue("5");
	        createPage.getUserAddress().setInputValue("6");
	        createPage.getFeederAddress().setTrueFalseByName("1", true);
	        
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
	    public void LoadGroupRfnExpresscom_GeographicalAddressingGeo_ValidIntegerValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("GEO", true);
	        createPage.getGeoAddress().setInputValue("aaa");
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getGeoAddress().getValidationError()).isEqualTo("Must be a valid integer value."); 
		       
	    }
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingGeo_ValidMinValueValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("GEO", true);
	        createPage.getGeoAddress().setInputValue("-1");
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getGeoAddress().getValidationError()).isEqualTo("Must be between 1 and 65,534."); 
		       
	    }
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingGeo_ValidMaxValueValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("GEO", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getGeoAddress().setInputValue("65535");
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getGeoAddress().getValidationError()).isEqualTo("Must be between 1 and 65,534."); 
		       
	    }
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingSubstation_ValidIntegerValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("Substation", true);
	        createPage.getSubstationAddress().setInputValue("aaa");
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getSubstationAddress().getValidationError()).isEqualTo("Must be a valid integer value."); 
	    } 
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingSubstation_ValidMinValueValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("Substation", true);

	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getSubstationAddress().setInputValue("-1");
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getSubstationAddress().getValidationError()).isEqualTo("Must be between 1 and 65,534."); 
	    }
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingSubstation_ValidMaxValueValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("Substation", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getSubstationAddress().setInputValue("65535");
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getSubstationAddress().getValidationError()).isEqualTo("Must be between 1 and 65,534."); 
	    } 
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingUser_ZipRequiredWhenProgramSelectedValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("ZIP", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getZipAddress().getValidationError()).isEqualTo("Zip is required."); 
		    
	    } 
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingZip_ValidMinValueValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("ZIP", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getZipAddress().setInputValue("-1");
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getZipAddress().getValidationError()).isEqualTo("Must be between 1 and 16,777,214."); 
	    }
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingZip_ValidMaxValueValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("ZIP", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getZipAddress().setInputValue("16777216");
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getZipAddress().getValidationError()).isEqualTo("Must be between 1 and 16,777,214."); 
	    }
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingUser_UserRequiredWhenProgramSelectedValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("User", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getUserAddress().getValidationError()).isEqualTo("User is required.");    	
	    } 
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingUser_ValidMinValueValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("User", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getUserAddress().setInputValue("-1");	        
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getUserAddress().getValidationError()).isEqualTo("Must be between 1 and 65,534.");    	
	    } 
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingUser_ValidMaxValueValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("User", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getUserAddress().setInputValue("65535");	        
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getUserAddress().getValidationError()).isEqualTo("Must be between 1 and 65,534.");    	
	    }
	    
	    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressingSerial_SerialRequiredWhenProgramSelectedValidation() {
	    	createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getAddressUsage().setTrueFalseByName("Serial", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getSaveBtn().click();
	        assertThat(createPage.getSerialAddress().getValidationError()).isEqualTo("Serial Number is required."); 
	       
	    } 
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_General_SectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        Section generalSection = createPage.getPageSection("General");
	        
	        assertThat(generalSection.getSection()).isNotNull();
	    }

	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_General_SectionLabelsCorrect() {
	        String sectionName = "General";
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:"));
	        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

	        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddress_SectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        Section generalSection = createPage.getPageSection("Geographical Address");
	        
	        assertThat(generalSection.getSection()).isNotNull();
	    }

	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddress_SectionLabelsCorrect() {
	        String sectionName = "Geographical Address";
	        String expectedLabels = "Address Usage:";
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();

	        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);
	        
	        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
	                .isTrue();
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressing_SectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        Section generalSection = createPage.getPageSection("Geographical Addressing");
	        
	        assertThat(generalSection.getSection()).isNotNull();
	    }

	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_GeographicalAddressing_SectionLabelsCorrect() {
	      String sectionName = "Geographical Addressing";
	      createPage.getType().selectItemByText("RFN Expresscom Group");
	      waitForLoadingSpinner();
	    createPage.getAddressUsage().setTrueFalseByName("GEO", true);
        createPage.getAddressUsage().setTrueFalseByName("Substation", true);
        createPage.getAddressUsage().setTrueFalseByName("Feeder", true);
        createPage.getAddressUsage().setTrueFalseByName("ZIP", true);
        createPage.getAddressUsage().setTrueFalseByName("User", true);
        createPage.getAddressUsage().setTrueFalseByName("Serial", true);
        
	        List<String> expectedLabels = new ArrayList<>(List.of("SPID:", "GEO:", "Substation:", "Feeder:", "ZIP:", "User:", "Serial:"));
	        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

	        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);

	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddress_SectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        Section generalSection = createPage.getPageSection("Load Address");
	        
	        assertThat(generalSection.getSection()).isNotNull();
	    }

	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddress_SectionLabelsCorrect() {
	        String sectionName = "Load Address";
	        String expectedLabels = "Usage:";
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();

	        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);
	        
	        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
	                .isTrue();
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressing_SectionLabelsCorrect() {
	        String sectionName = "Load Addressing";
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getLoadAddressUsage().setTrueFalseByName("Splinter", true);
		    
	        List<String> expectedLabels = new ArrayList<>(
	                List.of("Send Loads in Control Message:", "Loads:", "Program:", "Splinter:"));
	        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

	        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressing_SectionTitleCorrect() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();

	        Section generalSection = createPage.getPageSection("Load Addressing");
	        assertThat(generalSection.getSection()).isNotNull();
	    }
	    
	    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_OptionalAttributes_SectionLabelsCorrect() {
	        String sectionName = "Optional Attributes";
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner();

	        List<String> expectedLabels = new ArrayList<>(List.of("Control Priority:", "kW Capacity:", "Disable Group:", "Disable Control:"));
	        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

	        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressing_ProgramMinRangeValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getProgramLoadAddress().setInputValue("0");
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getProgramLoadAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressing_ProgramMaxRangeValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getProgramLoadAddress().setInputValue("255");
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getProgramLoadAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressing_ProgramBlankFieldValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Program", true);
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getProgramLoadAddress().getValidationError()).isEqualTo("Program is required.");
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressing_SplinterMinRangeValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Splinter", true);
	        createPage.getSplinterLoadAddress().setInputValue("0");
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getSplinterLoadAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressing_SplinterMaxRangeValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Splinter", true);
	        createPage.getSplinterLoadAddress().setInputValue("255");
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getSplinterLoadAddress().getValidationError()).isEqualTo("Must be between 1 and 254.");
	    }
	    
	    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.DemandResponse.DEMAND_RESPONSE})
	    public void LoadGroupRfnExpresscom_LoadAddressing_SplinterBlankFieldValidation() {
	        createPage.getType().selectItemByText("RFN Expresscom Group");
	        waitForLoadingSpinner(); 
	        createPage.getLoadAddressUsage().setTrueFalseByName("Splinter", true);
	        createPage.getSaveBtn().click();

	        assertThat(createPage.getSplinterLoadAddress().getValidationError()).isEqualTo("Splinter is required.");
	    }

	    
	    @AfterMethod(alwaysRun = true)
	    public void afterTest() {
	        refreshPage(createPage);
	        createPage = new LoadGroupRfnExpressCommCreatePage(driverExt);
	    }
	    
	}