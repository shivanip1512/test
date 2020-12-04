package com.eaton.tests.demandresponse.loadgroup;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.eaton.elements.Section;
import com.eaton.elements.modals.SelectPointModal;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.TestDbDataType;
import com.eaton.framework.Urls;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupDetailPage;
import com.eaton.pages.demandresponse.loadgroup.LoadGroupPointCreatePage;
import com.github.javafaker.Faker;

public class LoadGroupPointCreateTests extends SeleniumTestSetup {
    private LoadGroupPointCreatePage createPage;
    private DriverExtensions driverExt;
    private Faker faker;


    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);
        faker = SeleniumTestSetup.getFaker();

        navigate(Urls.DemandResponse.LOAD_GROUP_CREATE);
        createPage = new LoadGroupPointCreatePage(driverExt);
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        if(getRefreshPage()) {
            refreshPage(createPage);    
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_AllFields_Success() {
        setRefreshPage(true);
        String timeStamp = new SimpleDateFormat(TestConstants.DATE_FORMAT).format(System.currentTimeMillis());
        String name = "AT Point " + timeStamp;
        double capacity = faker.number().randomDouble(2, 1, 9999);

        final String EXPECTED_MSG = name + " saved successfully.";

        createPage.getType().selectItemByValue("LM_GROUP_POINT");

        waitForLoadingSpinner();
        createPage.getName().setInputValue(name);
        SelectPointModal pointGroupControlDevice = createPage.showAndWaitPointGroupControlDeviceModal();
        
        String controlDevicePtId = TestDbDataType.PointData.SCADA_OVERRIDE.getId().toString();
        String controlDevicePoint = TestDbDataType.PointData.SCADA_OVERRIDE.getName();
        pointGroupControlDevice.selectPointById(controlDevicePoint, controlDevicePtId);
        pointGroupControlDevice.clickOkAndWaitForModalCloseDisplayNone();
        
        createPage.getkWCapacity().setInputValue(String.valueOf(capacity));

        createPage.getDisableGroup().selectValue("Yes");
        createPage.getDisableControl().selectValue("Yes");

        createPage.getSaveBtn().click();

        waitForPageToLoad("Load Group: " + name, Optional.empty());

        LoadGroupDetailPage detailsPage = new LoadGroupDetailPage(driverExt);
        String userMsg = detailsPage.getUserMessage();

        assertThat(userMsg).isEqualTo(EXPECTED_MSG);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_PointGroupSection_TitleCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_POINT");
        waitForLoadingSpinner();
        Section section = createPage.getPageSection("Point Group");

        assertThat(section.getSection()).isNotNull();
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_PointGroupSection_LabelsCorrect() {
        String sectionName = "Point Group";
        String expectedLabels = "Control Device Point:";
        createPage.getType().selectItemByValue("LM_GROUP_POINT");
        waitForLoadingSpinner();

        String actualLabels = createPage.getPageSection(sectionName).getSectionLabels().get(0);

        assertThat(actualLabels).contains(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_ControlDevicePoint_RequiredValidation() {
        createPage.getType().selectItemByValue("LM_GROUP_POINT");
        waitForLoadingSpinner();

        createPage.getSaveBtn().click();

        assertThat(createPage.getControlDevicePointValidationMsg()).isEqualTo("Control Device Point is required.");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_ControlDevicePointLabel_DefaultValueCorrect() {
        createPage.getType().selectItemByValue("LM_GROUP_POINT");
        waitForLoadingSpinner();

        assertThat(createPage.getControlDevicePointLabelText()).isEqualTo("(none selected)");
    }

    @Test(groups = { TestConstants.Priority.LOW, TestConstants.Features.DEMAND_RESPONSE })
    public void ldGrpCreatePoint_ControlDevicePointLabel_UpdatedCorrectly() {
        setRefreshPage(true);
        createPage.getType().selectItemByValue("LM_GROUP_POINT");
        waitForLoadingSpinner();

        SelectPointModal pointGroupControlDevice = createPage.showAndWaitPointGroupControlDeviceModal();
        
        String controlDevicePtId = TestDbDataType.PointData.SCADA_OVERRIDE.getId().toString();
        String controlDevicePoint = TestDbDataType.PointData.SCADA_OVERRIDE.getName();
        pointGroupControlDevice.selectPointById(controlDevicePoint, controlDevicePtId);
        pointGroupControlDevice.clickOkAndWaitForModalCloseDisplayNone();

        assertThat(createPage.getControlDevicePointLabelText()).contains("SCADA Override");
    }
}
