package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

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

public class LoadGroupCreateTests extends SeleniumTestSetup {

    private LoadGroupCreatePage createPage;
    private DriverExtensions driverExt;
    private SoftAssertions softAssert;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {

        driverExt = getDriverExt();
        softAssert = new SoftAssertions();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS,
            TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_TitleCorrect() {
        final String EXPECTED_TITLE = "Create Load Group";

        String actualPageTitle = createPage.getPageTitle();

        assertThat(actualPageTitle).isEqualTo(EXPECTED_TITLE);
    }

    @Test(groups = { TestConstants.TestNgGroups.SMOKE_TESTS, TestConstants.TestNgGroups.REGRESSION_TESTS,
            TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_NameRequiredValidation() {
        createPage.getSaveBtn().click();

        assertThat(createPage.getName().getValidationError()).isEqualTo("Name is required.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_TypeRequiredValidation() {
        createPage.getSaveBtn().click();

        assertThat(createPage.getType().getValidationError()).isEqualTo("Type is required.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_NameInvalidCharValidation() {

        createPage.getName().setInputValue("test/,");
        createPage.getSaveBtn().click();

        assertThat(createPage.getName().getValidationError())
                .isEqualTo("Cannot be blank or include any of the following characters: / \\ , ' \" |");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_CancelButtonNavigatesToCorrectUrl() {
        String expectedURL = getBaseUrl() + Urls.DemandResponse.SETUP_FILTER + "LOAD_GROUP";

        createPage.getCancelBtn().click();
        ;
        String actualURL = getCurrentUrl();

        assertThat(actualURL).isEqualTo(expectedURL);
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_kWCapacityRequired() {

        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("kW Capacity is required.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_kWCapacityMaxRangeValidation() {
        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("1000000");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("Must be between 0 and 99,999.999.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_kWCapacityMinRangeValidation() {

        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().setInputValue("-1");
        createPage.getSaveBtn().click();

        assertThat(createPage.getkWCapacity().getValidationError()).isEqualTo("Must be between 0 and 99,999.999.");
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_GeneralSectionTitleCorrect() {

        Section general = createPage.getGeneralSection();
        assertThat(general.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_OptionalAttributeSectionTitleCorrect() {

        createPage.getType().selectItemByIndex(2);
        createPage.getkWCapacity().clearInputValue();
        ;
        Section optAttr = createPage.getOptionalAttributesSection();
        assertThat(optAttr.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.TestNgGroups.REGRESSION_TESTS, TestConstants.DEMAND_RESPONSE })
    public void ldGrpCreate_TypeDropDownContainsAllExpectedValues() {
        String expDropDownValues[] = { "Digi SEP Group", "ecobee Group", "Emetcon Group", "Expresscom Group", "Honeywell Group",
                "Itron Group", "MCT Group", "Meter Disconnect Group", "Point Group", "RFN Expresscom Group", "Ripple Group",
                "Versacom Group" };
        List<String> actualDropDownValues = createPage.getType().getOptionValues();

        for (String expdropDwonValue : expDropDownValues) {
            softAssert.assertThat(actualDropDownValues.contains(expdropDwonValue)).isTrue();
        }
        softAssert.assertAll();
    }
}
