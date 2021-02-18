package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.javatuples.Pair;
import org.json.JSONObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.builders.drsetup.loadgroup.LoadGroupEcobeeCreateBuilder;
import com.eaton.elements.Section;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupCreatePage;

public class LoadGroupCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupCreatePage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_Page_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Load Group";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_Name_RequiredValidation() {
        createPage.getName().clearInputValue();
        
        createPage.getSaveBtn().click();

        assertThat(createPage.getName().getValidationError()).isEqualTo("Name is required.");
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_Type_RequiredValidation() {
        createPage.getType().selectItemByIndex(0);
        
        createPage.getSaveBtn().click();

        assertThat(createPage.getType().getValidationError()).isEqualTo("Type is required.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_Name_InvalidCharValidation() {
        createPage.getName().setInputValue("test/,");
        createPage.getSaveBtn().click();

        assertThat(createPage.getName().getValidationError())
                .isEqualTo("Name must not contain any of the following characters: / \\ , ' \" |.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_Cancel_NavigatesToCorrectUrl() {
        setRefreshPage(true);
        createPage.getCancelBtn().click();

        String actualUrl = getCurrentUrl();

        assertThat(actualUrl).contains(Urls.DemandResponse.LOAD_GROUP_SETUP_LIST);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_KwCapacity_RequiredValidation() {
        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("kW Capacity is required.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_KwCapacity_MaxRangeValidation() {
        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("1000000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("Must be between 0 and 99,999.999.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_KwCapacity_MinRangeValidation() {
        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("Must be between 0 and 99,999.999.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_GeneralSection_TitleCorrect() {
        Section general = createPage.getPageSection("General");
        assertThat(general.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_OptionalAttributeSection_TitleCorrect() {
        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().clearInputValue();
        
        Section optAttr = createPage.getPageSection("Optional Attributes");
        assertThat(optAttr.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_Type_ValuesCorrect() {
        List<String> expectedDropDownValues = new ArrayList<>(List.of("Select", "Digi SEP Group", "ecobee Group", "Emetcon Group", "Expresscom Group", "Honeywell Group",
                "Itron Group", "MCT Group", "Meter Disconnect Group", "Point Group", "RFN Expresscom Group", "Ripple Group",
                "Versacom Group"));
        List<String> actualDropDownValues = createPage.getType().getOptionValues();
        
        assertThat(actualDropDownValues).containsExactlyElementsOf(expectedDropDownValues);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreate_Name_UniqueValidation() {
        Pair<JSONObject, JSONObject> pair = new LoadGroupEcobeeCreateBuilder.Builder(Optional.empty())
                .create();
        JSONObject response = pair.getValue1();
        String name = response.getString("name");

        createPage.getName().setInputValue(name);
        createPage.getType().selectItemByValue("LM_GROUP_ECOBEE");
        createPage.getkWCapacity().setInputValue("22");
        createPage.getSaveBtn().click();

        assertThat(createPage.getName().getValidationError()).isEqualTo("Name must be unique.");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateCommon_GeneralSection_LabelsCorrect() {
        String sectionName = "General";
        List<String> expectedLabels = new ArrayList<>(List.of("Name:", "Type:" ));

        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.DEMAND_RESPONSE})
    public void ldGrpCreateCommon_OptionalAttrSection_LabelsCorrect() {
        String sectionName = "Optional Attributes";
        List<String> expectedLabels = new ArrayList<>(List.of("kW Capacity:", "Disable Group:", "Disable Control:"));

        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("2");
        List<String> actualLabels = createPage.getPageSection(sectionName).getSectionLabels();
        
        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }
}