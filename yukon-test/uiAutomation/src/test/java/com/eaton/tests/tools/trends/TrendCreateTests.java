package com.eaton.tests.tools.trends;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.TrendAddMarkerModal;
import com.eaton.elements.modals.TrendPointModal;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.tools.trends.TrendCreatePage;
import com.eaton.pages.tools.trends.TrendsListPage;
import com.github.javafaker.Faker;

import io.restassured.response.ExtractableResponse;

import com.eaton.builders.tools.webtrends.TrendCreateBuilder;

public class TrendCreateTests extends SeleniumTestSetup {

        private Faker faker = new Faker();
        
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

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS})
    public void trendCreate_PageTitle_Correct() {
        final String EXPECTED_TITLE = "Create Trend";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }    
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS})
    public void trendCreate_AllFields_Successfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Trend " + timeStamp;
        
        String point = "Analog Point for Create Trend";
        String label = "AT Marker";
        
        final String EXPECTED_MSG = name + " saved successfully.";
        
        createPage.getName().setInputValue(name);
        
        //Adding Marker Setup
        createPage.getTabElement().clickTabAndWait("Additional Options");
        TrendAddMarkerModal addMarkerModal = createPage.showAndWaitAddMarkerModal();
        addMarkerModal.getLabel().setInputValue(label);
        addMarkerModal.clickOkAndWaitForModalToClose();

        //Adding Point Setup
        createPage.getTabElement().clickTabAndWait("Setup");
        TrendPointModal addPointModal = createPage.showAndWaitAddPointModal();
        SelectPointModal selectPointModal = addPointModal.showAndWaitSelectPointModal();
        selectPointModal.selectPoint(point, Optional.of("5231"));
        selectPointModal.clickOkAndWaitForModalToClose();
        addPointModal.clickOkAndWaitForModalToClose();
        
        createPage.getSave().click();
        
        waitForPageToLoad(name, Optional.of(3));

        TrendsListPage listPage = new TrendsListPage(driverExt);
        
        String userMsg = listPage.getUserMessage();
        
        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Tools.TRENDS })
    public void trendCreate_RequiredFieldsOnly_Successfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Trend " + timeStamp;
        
        final String EXPECTED_MSG = name + " saved successfully.";
        
        createPage.getName().setInputValue(name);
        createPage.getSave().click();
        
        TrendsListPage listPage = new TrendsListPage(driverExt);
        
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
  
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS })
    public void trendCreate_TabTitles_Correct() {
        SoftAssertions softly = new SoftAssertions();
        
        List<String> titles = createPage.getTabElement().getTitles();

        softly.assertThat(titles.size()).isEqualTo(2);
        softly.assertThat(titles.get(0)).isEqualTo("Setup");
        softly.assertThat(titles.get(1)).isEqualTo("Additional Options");
        softly.assertAll();
    }
  
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_GeneralSectionTitle_Correct() {
        String tab = "Setup";
        
        createPage.getTabElement().clickTabAndWait(tab);
 
        Section generalSection = createPage.getGeneralSection();
        
        assertThat(generalSection.getSection()).isNotNull();
    }
 
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_GeneralSectionLabels_Correct() {
        String tab = "Setup";
        
        createPage.getTabElement().clickTabAndWait(tab);
        
        List<String> expectedLabels = new ArrayList<>(List.of("Name:"));
        List<String> actualLabels = createPage.getGeneralSection().getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_PointSetupSectionTitle_Correct() {
        String tab = "Setup";
        
        createPage.getTabElement().clickTabAndWait(tab);
        
        Section generalSection = createPage.getPointSetupSection();
        
        assertThat(generalSection.getSection()).isNotNull();
    }
   
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_PointSectionTableHeaders_Correct() {
        String tab = "Setup";
        
        createPage.getTabElement().clickTabAndWait(tab);
        
        List<String> expectedLabels = new ArrayList<>(List.of("Device", "Point Name", "Label", "Color", "Axis", "Type", "Multiplier", "Style"));
        List<String> actualLabels = createPage.getPointSetupTable().getListTableHeaders();
        actualLabels.remove("");

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_MarkerSetupSectionTitle_Correct() {
        String tab = "Additional Options";
        
        createPage.getTabElement().clickTabAndWait(tab);
        
        Section generalSection = createPage.getMarkerSetupSection();
        
        assertThat(generalSection.getSection()).isNotNull();
    }
   
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_MarkerSetupTableHeaders_Correct() {
        String tab = "Additional Option";
        
        createPage.getTabElement().clickTabAndWait(tab);
        
        List<String> expectedLabels = new ArrayList<>(List.of("Label", "Color", "Axis", "Value", ""));
        
        List<String> actualLabels = createPage.getMarkerSetupTable().getListTableHeaders();
                        
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_Name_RequiredValidation() {
         final String EXPECTED_MSG = "Name is required.";

         createPage.getSave().click();
         
         String errorMsg = createPage.getName().getValidationError();

         assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
   
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_Name_AlreadyExistsValidation() {
                 String trendName;
                 
                 final String EXPECTED_MSG = "Name already exists";
                 
                 Pair<JSONObject, ExtractableResponse<?>> pair = new TrendCreateBuilder.Builder(Optional.empty())
                                                                                                                        .create();
                
                 ExtractableResponse<?> response = pair.getValue1();
                 
                 trendName = response.path("name").toString();
                
                 navigate(Urls.Tools.TREND_CREATE);
                 
                 createPage.getName().setInputValue(trendName);
                 createPage.getSave().click();
        
                 String errorMsg = createPage.getName().getValidationError();   

         assertThat(errorMsg).isEqualTo(EXPECTED_MSG);  
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_Cancel_NavigatesToCorrectUrl() {
        final String EXPECTED_TITLE = "Trend";

        createPage.getCancel().click();
        
        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).contains(EXPECTED_TITLE);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_Name_InvalidCharsValidation() {
        String name = "AT Trends " + "/ \\ , ' \" |";
        
        final String EXPECTED_MSG = "Name must not contain any of the following characters: / \\ , ' \" |.";
        
        createPage.getName().setInputValue(name);
        createPage.getSave().click();
        
        String errorMsg = createPage.getName().getValidationError();    

        assertThat(errorMsg).isEqualTo(EXPECTED_MSG);
    }
    
    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Tools.TRENDS})
    public void trendCreate_Name_MaxLength40CharsSuccess() {
        String name = "AT Trend" + faker.number().digits(32);
        
        final String EXPECTED_MSG = name + " saved successfully.";
        
        createPage.getName().setInputValue(name);
        createPage.getSave().click();
        
        TrendsListPage listPage = new TrendsListPage(driverExt);
        
        String userMsg = listPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }
}