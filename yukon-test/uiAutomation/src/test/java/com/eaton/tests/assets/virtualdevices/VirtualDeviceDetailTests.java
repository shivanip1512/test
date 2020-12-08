package com.eaton.tests.assets.virtualdevices;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.testng.annotations.Test;
import org.json.JSONObject;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateService;
import com.eaton.elements.WebTable.SortDirection;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.RecentArchievedRadingsModal;
import com.eaton.elements.modals.virtualdevices.CreateVirtualDeviceModal;
import com.eaton.elements.modals.virtualdevices.EditVirtualDeviceModal;
import com.eaton.elements.panels.VirtualDevicePointsPanel;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.TestConstants;
import com.eaton.framework.Urls;
import com.eaton.pages.assets.virtualdevices.VirtualDevicesDetailPage;
import com.eaton.rest.api.common.ApiCallHelper;

import io.restassured.response.ExtractableResponse;

public class VirtualDeviceDetailTests extends SeleniumTestSetup {

    private VirtualDevicesDetailPage detailPage;
    private DriverExtensions driverExt;
    private Integer virtualDeviceId;
    private String virtualDeviceName;
    private String virtualDeviceStatus;
    private String type;
    private Integer analogPtId;

    private List<String> pointNames;
    private List<String> pointTypes;
    private List<String> pointOffsets;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        Boolean enable;

        Map<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.createVirtualDeviceWithAllPoints(Optional.empty());

        // Virtual Device Response
        Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");
        JSONObject virDevResponse = virtualDevice.getValue1();

        virtualDeviceId = virDevResponse.getInt("id");
        virtualDeviceName = virDevResponse.getString("name");
        type = virDevResponse.getString("type");

        enable = virDevResponse.getBoolean("enable");

        virtualDeviceStatus = (enable.equals(true)) ? "Enabled" : "Disabled";

        // Points Response
        analogPtId = pair.get("AnalogPoint").getValue1().getInt("pointId");

        navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);

        pointNames = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(1);
        pointTypes = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
        pointOffsets = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(7);
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        if (getRefreshPage()) {
            refreshPage(detailPage);
        }
        setRefreshPage(false);
    }

    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_PageTitle_Correct() {
        setRefreshPage(false);

        String expectedTitle = virtualDeviceName;

        String actualPageTitle = detailPage.getPageTitle();

        assertThat(expectedTitle).isEqualTo(actualPageTitle);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_InfoSection_Displayed() {
        setRefreshPage(false);

        String expectedPanelText = "Virtual Device Information";

        String actualPanelText = detailPage.getVirtualDeviceInfoPanel().getPanelName();

        assertThat(actualPanelText).isEqualTo(expectedPanelText);
    }

    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DevicePoints_Displayed() {
        setRefreshPage(false);

        String expectedPanelText = "Device Points";

        String actualPanelText = detailPage.getVirtualDevicePointsPanel().getPanelName();

        assertThat(actualPanelText).isEqualTo(expectedPanelText);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_InfoFieldLabels_Correct() {
        setRefreshPage(false);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelCount()).isEqualTo(3);
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelByRow(0)).isEqualTo("Name:");
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelByRow(1)).isEqualTo("Type:");
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelByRow(2)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_InfoFieldValues_Correct() {
        setRefreshPage(false);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueCount()).isEqualTo(3);
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueByRow(0))
                .isEqualTo(virtualDeviceName);
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueByRow(1).toUpperCase())
                .isEqualTo(type.replace("_", " "));
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueByRow(2))
                .isEqualTo(virtualDeviceStatus);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_Edit_OpensCorrectModal() {
        setRefreshPage(true);

        final String EXP_MODAL_TITLE = "Edit " + virtualDeviceName;

        EditVirtualDeviceModal editModal = detailPage.showAndWaitEditVirtualDeviceModal();

        String title = editModal.getModalTitle();

        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_Create_OpensCorrectModal() {
        setRefreshPage(true);

        final String EXP_MODAL_TITLE = "Create Virtual Device";
        waitForLoadingSpinner();
        CreateVirtualDeviceModal createModal = detailPage.showAndWaitCreateVirtualDeviceModal();

        String title = createModal.getModalTitle();

        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_OtherActionsUrl_Correct() {
        setRefreshPage(true);

        SoftAssertions softly = new SoftAssertions();

        final String OTHER_ACTIONS_URL = getBaseUrl() + "/" + Urls.OTHER_ACTIONS + virtualDeviceId;

        String actualOtherActionUrl = detailPage.getActionBtn().getOptionLinkByText("Other Actions");

        ExtractableResponse<?> response = ApiCallHelper.get(OTHER_ACTIONS_URL);

        // Validation for URL
        softly.assertThat(actualOtherActionUrl).isEqualTo(OTHER_ACTIONS_URL);
        // Validation for response code
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_Delete_OpensCorrectModal() {
        setRefreshPage(true);

        String expectedModalTitle = "Confirm Delete";

        ConfirmModal deleteConfirmModal = detailPage.showAndWaitDeleteVirtualDeviceModal();

        String actualModalTitle = deleteConfirmModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_CreateAnalogPointUrl_Correct() {
        setRefreshPage(true);

        SoftAssertions softly = new SoftAssertions();

        final String EXP_ANLG_POINT_URL = getBaseUrl() + Urls.Tools.ANALOG_POINT + virtualDeviceId;

        String actualAnalogPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn().getOptionLinkByText("Analog Point");

        ExtractableResponse<?> response = ApiCallHelper.get(EXP_ANLG_POINT_URL);

        // Assert for URL
        softly.assertThat(actualAnalogPtUrl).isEqualTo(EXP_ANLG_POINT_URL);
        // Assert for response code
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_CreateCalcAnalogPointUrl_Correct() {
        setRefreshPage(true);

        SoftAssertions softly = new SoftAssertions();

        final String EXP_CALC_ANLG_POINT_URL = getBaseUrl() + Urls.Tools.CALC_ANALOG_POINT + virtualDeviceId;

        String actualCalcAnlgPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn()
                .getOptionLinkByText("Calc Analog Point");

        ExtractableResponse<?> response = ApiCallHelper.get(EXP_CALC_ANLG_POINT_URL);

        // Assert for URL
        softly.assertThat(actualCalcAnlgPtUrl).isEqualTo(EXP_CALC_ANLG_POINT_URL);
        // Assert for response code
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_CreateCalcStatusPointUrl_Correct() {
        setRefreshPage(true);

        SoftAssertions softly = new SoftAssertions();

        final String EXP_CALC_STS_POINT_URL = getBaseUrl() + Urls.Tools.CALC_STATUS_POINT + virtualDeviceId;

        String actualCalcStsPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn()
                .getOptionLinkByText("Calc Status Point");

        ExtractableResponse<?> response = ApiCallHelper.get(EXP_CALC_STS_POINT_URL);

        // Validation for URL
        softly.assertThat(actualCalcStsPtUrl).isEqualTo(EXP_CALC_STS_POINT_URL);
        // Validation for response code
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_CreateDemandAccPointUrl_Correct() {
        setRefreshPage(true);

        SoftAssertions softly = new SoftAssertions();

        final String EXP_DMND_ACC_POINT_URL = getBaseUrl() + Urls.Tools.DEMAND_ACCUMULATOR_POINT + virtualDeviceId;

        String actualCalcStsPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn()
                .getOptionLinkByText("Demand Accumulator Point");

        ExtractableResponse<?> response = ApiCallHelper.get(EXP_DMND_ACC_POINT_URL);

        // Validation for URL
        softly.assertThat(actualCalcStsPtUrl).isEqualTo(EXP_DMND_ACC_POINT_URL);
        // Validation for response code
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_CreatePulseAccPointUrl_Correct() {
        setRefreshPage(true);

        SoftAssertions softly = new SoftAssertions();

        final String EXP_PLS_ACC_POINT_URL = getBaseUrl() + Urls.Tools.PULSE_ACCUMULATOR_POINT + virtualDeviceId;

        String actualCalcStsPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn()
                .getOptionLinkByText("Pulse Accumulator Point");

        ExtractableResponse<?> response = ApiCallHelper.get(EXP_PLS_ACC_POINT_URL);

        // Validation for URL
        softly.assertThat(actualCalcStsPtUrl).isEqualTo(EXP_PLS_ACC_POINT_URL);
        // Validation for response code
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_CreateStatusPointUrl_Correct() {
        setRefreshPage(true);

        SoftAssertions softly = new SoftAssertions();

        final String EXP_STS_POINT_URL = getBaseUrl() + Urls.Tools.STATUS_POINT + virtualDeviceId;

        String actualCalcStsPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn().getOptionLinkByText("Status Point");

        ExtractableResponse<?> response = ApiCallHelper.get(EXP_STS_POINT_URL);

        // Validation for URL
        softly.assertThat(actualCalcStsPtUrl).isEqualTo(EXP_STS_POINT_URL);
        // Validation for response code
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DevPtsPointNameUrl_Correct() {
        setRefreshPage(false);

        SoftAssertions softly = new SoftAssertions();

        final String EXP_POINT_URL = getBaseUrl() + Urls.Tools.POINT + analogPtId;

        String pointUrl = detailPage.getPointsTableRow(0).getCellLinkByIndex(0);

        ExtractableResponse<?> response = ApiCallHelper.get(pointUrl);

        // Validation for URL
        softly.assertThat(EXP_POINT_URL).isEqualTo(pointUrl);
        // Validation for response code
        softly.assertThat(response.statusCode()).isEqualTo(200);
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DevPtsDateTime_OpensCorrectModal() {
        setRefreshPage(true);

        final String EXP_MODAL_TITLE = "Recent Archived Readings";

        RecentArchievedRadingsModal rcntArchReadingsModal = detailPage
                .showAndWaitRecentArchievedReadingsModal("Recent Archived Readings", 5);

        String title = rcntArchReadingsModal.getModalTitle();

        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByStatus_Correct() {
        setRefreshPage(true);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();
        panel.getPointType().selectItemByText("Status");
        panel.getFilter().click();

        panel.getTable().waitForFilter();

        List<String> pointTypeList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypeList).containsOnly("Status");

    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByAnalogPt_Correct() {
        setRefreshPage(true);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();
        panel.getPointType().selectItemByText("Analog");
        panel.getFilter().click();

        panel.getTable().waitForFilter();

        List<String> pointTypeList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypeList).containsOnly("Analog");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByCalcStatusPt_Correct() {
        setRefreshPage(true);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();
        panel.getPointType().selectItemByText("Calc Status");
        panel.getFilter().click();

        panel.getTable().waitForFilter();

        List<String> pointTypeList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypeList).containsOnly("Calc Status");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByDemandAccPt_Correct() {
        setRefreshPage(true);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();
        panel.getPointType().selectItemByText("Demand Accumulator");
        panel.getFilter().click();

        panel.getTable().waitForFilter();

        List<String> pointTypeList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypeList).containsOnly("Demand Accumulator");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByPulseAccPt_Correct() {
        setRefreshPage(true);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();
        panel.getPointType().selectItemByText("Pulse Accumulator");
        panel.getFilter().click();

        panel.getTable().waitForFilter();

        List<String> pointTypeList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypeList).containsOnly("Pulse Accumulator");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByCalcAnalogPt_Correct() {
        setRefreshPage(true);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();
        panel.getPointType().selectItemByText("Calc Analog");
        panel.getFilter().click();

        panel.getTable().waitForFilter();

        List<String> pointTypeList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypeList).containsOnly("Calc Analog");
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_RemoveFilter_CorrectPointsDisplayed() {
        setRefreshPage(true);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();
        panel.getPointType().selectItemByText("Calc Analog");
        panel.getFilter().click();

        panel.getTable().waitForFilter();

        panel.getPointType().removeItemByIndex(0);
        panel.getFilter().click();

        panel.getTable().waitForClearFilter();

        List<String> pointType = new ArrayList<>(List.of("Analog", "Calc Analog", "Calc Status", "Demand Accumulator", "Pulse Accumulator", "Status"));

        List<String> pointTypeList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypeList).containsExactlyElementsOf(pointType);

    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterMultiplePoints_Correct() {
        setRefreshPage(true);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();
        panel.getPointType().selectItemByText("Calc Analog");
        panel.getPointType().selectItemByText("Status");

        panel.getFilter().click();

        panel.getTable().waitForFilter();

        String[] pointType = { "Calc Analog", "Status" };

        List<String> pointTypeList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypeList).containsOnly(pointType);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DeleteModalMessage_Correct() {
        setRefreshPage(true);

        final String EXP_DELETE_MODAL_MSG = "Are you sure you want to delete \"" + virtualDeviceName + "\"?";

        ConfirmModal deleteModal = detailPage.showAndWaitDeleteVirtualDeviceModal();

        String deleteMsg = deleteModal.getConfirmMsg();

        assertThat(EXP_DELETE_MODAL_MSG).isEqualTo(deleteMsg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DeleteWithPt_Success() {
        setRefreshPage(true);

        Map<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.createVirtualDeviceWithAnalogPoint();
        Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");

        JSONObject virtDevResponse = virtualDevice.getValue1();

        Integer id = virtDevResponse.getInt("id");
        String name = virtDevResponse.getString("name");

        navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);

        final String EXP_MSG = name + " deleted successfully.";

        ConfirmModal deleteModal = detailPage.showAndWaitDeleteVirtualDeviceModal();
        deleteModal.clickOkAndWaitForModalToClose();

        String actualMsg = detailPage.getUserMessage();

        assertThat(EXP_MSG).isEqualTo(actualMsg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DeleteWithOutPt_Success() {
        setRefreshPage(true);

        Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.createVirtualDeviceOnlyRequiredFields();

        JSONObject virtDevResponse = pair.getValue1();

        Integer id = virtDevResponse.getInt("id");
        String name = virtDevResponse.getString("name");

        navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);

        final String EXP_MSG = name + " deleted successfully.";

        ConfirmModal deleteModal = detailPage.showAndWaitDeleteVirtualDeviceModal();
        deleteModal.clickOkAndWaitForModalToClose();

        String actualMsg = detailPage.getUserMessage();

        assertThat(EXP_MSG).isEqualTo(actualMsg);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DevPtsColumnHeaders_Correct() {
        setRefreshPage(false);

        List<String> expectedLabels = new ArrayList<>(
                List.of("Point Name", "Attribute", "", "Value/State", "Date/Time", "Point Type", "Point Offset"));

        List<String> actualLabels = detailPage.getVirtualDevicePointsPanel().getTable().getListTableHeaders();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DevPtsSortPointNamesAsc_Correctly() {
        setRefreshPage(true);

        Collections.sort(pointNames);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();

        panel.getTable().sortTableHeaderByIndex(0, SortDirection.ASCENDING);

        List<String> pointNamesList = panel.getTable().getDataRowsTextByCellIndex(1);

        assertThat(pointNames).isEqualTo(pointNamesList);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DevPtsSortPointNamesDesc_Correctly() {
        setRefreshPage(true);

        Collections.sort(pointNames);
        Collections.reverse(pointNames);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();

        panel.getTable().sortTableHeaderByIndex(0, SortDirection.DESCENDING);

        List<String> pointNamesList = panel.getTable().getDataRowsTextByCellIndex(1);

        assertThat(pointNames).isEqualTo(pointNamesList);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortPointTypeAsc_Correctly() {
        setRefreshPage(true);

        Collections.sort(pointTypes);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();

        panel.getTable().sortTableHeaderByIndex(5, SortDirection.ASCENDING);

        List<String> pointTypesList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypes).isEqualTo(pointTypesList);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortPointTypeDesc_Correctly() {
        setRefreshPage(true);

        Collections.sort(pointTypes);
        Collections.reverse(pointTypes);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();

        panel.getTable().sortTableHeaderByIndex(5, SortDirection.DESCENDING);

        List<String> pointTypesList = panel.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypes).isEqualTo(pointTypesList);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortPointOffsetAsc_Correctly() {
        setRefreshPage(true);
        List<Integer> listOfPointOffsets = new ArrayList<Integer>();
        listOfPointOffsets.addAll(pointOffsets.stream().map(Integer::valueOf).collect(Collectors.toList()));
        Collections.sort(listOfPointOffsets);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();

        panel.getTable().sortTableHeaderByIndex(6, SortDirection.ASCENDING);

        List<String> pointOffsetList = panel.getTable().getDataRowsTextByCellIndex(7);
        List<Integer> offsetList = new ArrayList<Integer>();
        offsetList.addAll(pointOffsetList.stream().map(Integer::valueOf).collect(Collectors.toList()));
        assertThat(listOfPointOffsets).isEqualTo(offsetList);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortPointOffsetDesc_Correctly() {
        setRefreshPage(true);
        List<Integer> listOfPointOffsets = new ArrayList<Integer>();
        listOfPointOffsets.addAll(pointOffsets.stream().map(Integer::valueOf).collect(Collectors.toList()));
        Collections.sort(listOfPointOffsets);
        Collections.reverse(listOfPointOffsets);

        VirtualDevicePointsPanel panel = detailPage.getVirtualDevicePointsPanel();

        panel.getTable().sortTableHeaderByIndex(6, SortDirection.DESCENDING);

        List<String> pointOffsetList = panel.getTable().getDataRowsTextByCellIndex(7);
        List<Integer> offsetList = new ArrayList<Integer>();
        offsetList.addAll(pointOffsetList.stream().map(Integer::valueOf).collect(Collectors.toList()));
        assertThat(listOfPointOffsets).isEqualTo(offsetList);
    }
}
