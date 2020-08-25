package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.WebTableRow;
import com.eaton.elements.WebTableRow.Icon;
import com.eaton.elements.modals.TrendAddMarkerModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendCreatePage;

public class TrendMarkerSetupTests extends SeleniumTestSetup{

	private TrendCreatePage createPage;
    private DriverExtensions driverExt;
    
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();

        navigate(Urls.Tools.TREND_CREATE);
        createPage = new TrendCreatePage(driverExt, Urls.Tools.TREND_CREATE);
    }

    @AfterMethod
    public void afterMethod() {
        refreshPage(createPage);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_Add_OpensCorrectModal() {
    	final String EXP_MODAL_TITLE = "Add Marker";
    			
    	createPage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = createPage.showAndWaitAddMarkerModal();
   
    	String title = addMarkerModal.getModalTitle();
    	
        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }
   
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_AddMarkerLabels_Correct() {
    	SoftAssertions softly = new SoftAssertions();
    	
    	createPage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = createPage.showAndWaitAddMarkerModal();
    	
    	List<String> labels = addMarkerModal.getFieldLabels();
    	
    	softly.assertThat(labels.size()).isEqualTo(4);
    	softly.assertThat(labels.get(0)).isEqualTo("Value:");
        softly.assertThat(labels.get(1)).contains("Label:");
        softly.assertThat(labels.get(2)).contains("Color:");
        softly.assertThat(labels.get(3)).contains("Axis:");
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_AddMarkerLabel_RequiredValidation() {
    	final String EXPECTED_MSG = "Label is required.";
    	
    	createPage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = createPage.showAndWaitAddMarkerModal();
    	
    	addMarkerModal.clickOkAndWait();
    	
    	String userMsg = addMarkerModal.getLabel().getValidationError();
    	
    	assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_AddMarkerLabel_MaxLength40CharsSuccess() {
    	final String EXPECTED_MAXLENGTH = "40";
    	createPage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = createPage.showAndWaitAddMarkerModal();
    	
    	String labelMaxlLength = addMarkerModal.getLabelMaxLength();
    	
    	assertThat(labelMaxlLength).isEqualTo(EXPECTED_MAXLENGTH);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_AddMarkerValue_InvalidCharValidation() {
    	final String EXPECTED_MSG = "Not a valid value.";
    	
    	String value = "!@Test";
    	
    	createPage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = createPage.showAndWaitAddMarkerModal();
    	
    	addMarkerModal.getValue().setInputValue(value);
    	
    	addMarkerModal.clickOkAndWait();
    	
    	String userMsg = addMarkerModal.getValue().getValidationError();
    	
    	assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
   
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_EditMarker_OpensCorrectModal() {
    	String label = "AT Marker";
    	
    	final String EXP_MODAL_TITLE = "Edit " + label;
    	
    	createPage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = createPage.showAndWaitAddMarkerModal();
    	addMarkerModal.getLabel().setInputValue(label);
    	addMarkerModal.clickOkAndWait();
    	
    	WebTableRow row = createPage.getMarkerSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.PENCIL);
        
        TrendAddMarkerModal modal = new TrendAddMarkerModal(this.driverExt, Optional.of(EXP_MODAL_TITLE), Optional.empty());
        
        String title = modal.getModalTitle();
    	
        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }
 
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendMarkerSetup_EditMarkerFieldValues_Correct() {
    	SoftAssertions softly = new SoftAssertions();
    	
    	String label = "AT Marker";
    	
    	createPage.getTabElement().clickTabAndWait("Additional Options");
    	TrendAddMarkerModal addMarkerModal = createPage.showAndWaitAddMarkerModal();
    	addMarkerModal.getLabel().setInputValue(label);
    	addMarkerModal.clickOkAndWait();
    	
    	WebTableRow row = createPage.getMarkerSetupTable().getDataRowByIndex(0);
        row.clickIcon(Icon.PENCIL);
        
        TrendAddMarkerModal modal = new TrendAddMarkerModal(this.driverExt, Optional.of("Edit " + label), Optional.empty());
    	
        softly.assertThat(modal.getValue().getInputValue()).isEqualTo("1.0");
        softly.assertThat(modal.getLabel().getInputValue()).isEqualTo(label);
        softly.assertThat(modal.getAxis().getValueChecked()).isEqualTo("LEFT");
        softly.assertAll();
    }
    
}
