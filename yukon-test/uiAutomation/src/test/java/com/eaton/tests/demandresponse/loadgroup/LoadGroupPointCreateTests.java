package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.LoadGroupPointCreatePage;

public class LoadGroupPointCreateTests extends SeleniumTestSetup {

    private LoadGroupPointCreatePage createPage;
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
        createPage = new LoadGroupPointCreatePage(driverExt);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_AllFieldsSuccessfully() {
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Point " + timeStamp;
        double randomDouble = randomNum.nextDouble();
        int randomInt = randomNum.nextInt(9999);
        double capacity = randomDouble + randomInt;

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByText("Point Group");

        waitForLoadingSpinner();
        createPage.getName().setInputValue(name);
        SelectPointModal pointGroupControlDevice = createPage.showAndWaitPointGroupControlDeviceModal("Select Control Device");
        pointGroupControlDevice.selectPointGroupControlDeviceTable("SCADA Override");
        pointGroupControlDevice.clickOkAndWaitForModalToClose();
        ;
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getDisableGroup().setValue(true);
        createPage.getDisableControl().setValue(true);

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_PointGroupSectionTitleCorrect() {
        createPage.getType().selectItemByText("Point Group");
        waitForLoadingSpinner();
        Section section = createPage.getPageSection("Point Group");

        assertThat(section.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_PointGroupSectionLabelsCorrect() {
        String sectionName = "Point Group";
        String expectedLabels = "Control Device Point:";
        createPage.getType().selectItemByText("Point Group");
        waitForLoadingSpinner();

        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);

        assertThat(actualLabels.contains(expectedLabels)).withFailMessage("Assertion failed for label : " + expectedLabels)
                .isTrue();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_ControlDevicePointBlankValue() {
        createPage.getType().selectItemByText("Point Group");
        waitForLoadingSpinner();

        createPage.getSaveBtn().click();

        assertThat(createPage.getControlDevicePoint().getValidationError("deviceUsage.id"))
                .isEqualTo("Control Device Point is required.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_ControlDevicePointLabelDefaultValue() {
        createPage.getType().selectItemByText("Point Group");
        waitForLoadingSpinner();

        assertThat(createPage.getControlDevicePointLabelText()).isEqualTo("(none selected)");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.DemandResponse.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_ControlDevicePointLabelValueAfterPointSelection() {
        createPage.getType().selectItemByText("Point Group");
        waitForLoadingSpinner();

        SelectPointModal pointGroupControlDevice = createPage.showAndWaitPointGroupControlDeviceModal("Select Control Device");
        pointGroupControlDevice.selectPointGroupControlDeviceTable("SCADA Override");
        pointGroupControlDevice.clickOkAndWaitForModalToClose();

        assertThat(createPage.getControlDevicePointLabelText()).contains("SCADA Override");
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        refreshPage(createPage);
        createPage = new LoadGroupPointCreatePage(driverExt);
    }
}
