package com.eaton.tests.assets.virtualdevices;

import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.javatuples.Pair;
import org.testng.annotations.Test;
import org.json.JSONObject;

import com.eaton.builders.assets.virtualdevices.VirtualDeviceCreateService;
import com.eaton.elements.modals.ConfirmModal;
import com.eaton.elements.modals.RecentArchievedRadingsModal;
import com.eaton.elements.modals.virtualdevices.CreateVirtualDeviceModal;
import com.eaton.elements.modals.virtualdevices.EditVirtualDeviceModal;
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
    private Integer analogPtId;

    // ===================================================================================================
    // Below code Commented due to YUK-23130
    // ===================================================================================================
    // private List<String> pointNames;
    // private List<String> pointTypes;
    // private List<String> pointOffsets;
    // private List<String> pointStatus;

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        driverExt = getDriverExt();
        setRefreshPage(false);

        Boolean enable;

        Map<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints(Optional.empty());

        // Virtual Device Response
        Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");
        JSONObject virDevResponse = virtualDevice.getValue1();

        virtualDeviceId = virDevResponse.getInt("id");
        virtualDeviceName = virDevResponse.getString("name");
        enable = virDevResponse.getBoolean("enable");

        virtualDeviceStatus = (enable.equals(true)) ? "Enabled" : "Disabled";

        // Points Response
        analogPtId = pair.get("AnalogPoint").getValue1().getInt("pointId");

        navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);

        // ===================================================================================================
        // Below code Commented due to YUK-23130
        // ===================================================================================================
        // pointNames = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(1);
        // pointStatus = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(2);
        // pointTypes = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
        // pointOffsets = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(7);

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

        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelCount()).isEqualTo(2);
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelByRow(0)).isEqualTo("Name:");
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelByRow(1)).isEqualTo("Status:");
        softly.assertAll();
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_InfoFieldValues_Correct() {
        setRefreshPage(false);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueCount()).isEqualTo(2);
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueByRow(0)).isEqualTo(virtualDeviceName);
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueByRow(1)).isEqualTo(virtualDeviceStatus);
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

        String actualCalcAnlgPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn().getOptionLinkByText("Calc Analog Point");

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

        String actualCalcStsPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn().getOptionLinkByText("Calc Status Point");

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

        String actualCalcStsPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn().getOptionLinkByText("Demand Accumulator Point");

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

        String actualCalcStsPtUrl = detailPage.getVirtualDevicePointsPanel().getCreateBtn().getOptionLinkByText("Pulse Accumulator Point");

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

        String pointUrl = detailPage.getPointsTableRow(1).getCellLinkByIndex(0);

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

        RecentArchievedRadingsModal rcntArchReadingsModal = detailPage.showAndWaitRecentArchievedReadingsModal("Recent Archived Readings", 5);

        String title = rcntArchReadingsModal.getModalTitle();

        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByStatus_Correct() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================

        /*
         * setRefreshPage(true);
         * detailPage.getPointType().selectItemByText("Status");
         * detailPage.getFilter().click();
         * 
         * List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypeList).containsOnly("Status");
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByAnalogPt_Correct() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * 
         * detailPage.getPointType().selectItemByText("Analog");
         * detailPage.getFilter().click();
         * 
         * List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypeList).containsOnly("Analog");
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByCalcStatusPt_Correct() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * 
         * detailPage.getPointType().selectItemByText("Calc Status");
         * detailPage.getFilter().click();
         * 
         * List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypeList).containsOnly("Calc Status");
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByDemandAccPt_Correct() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * 
         * detailPage.getPointType().selectItemByText("Demand Accumulator");
         * detailPage.getFilter().click();
         * 
         * List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypeList).containsOnly("Demand Accumulator");
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByPulseAccPt_Correct() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * 
         * detailPage.getPointType().selectItemByText("Pulse Accumulator");
         * detailPage.getFilter().click();
         * 
         * List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypeList).containsOnly("Pulse Accumulator");
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterPointTypeByCalcAnalogPt_Correct() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * 
         * detailPage.getPointType().selectItemByText("Calc Analog");
         * detailPage.getFilter().click();
         * 
         * List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypeList).containsOnly("Calc Analog");
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_RemoveFilter_CorrectPointsDisplayed() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * 
         * detailPage.getPointType().selectItemByText("Calc Analog");
         * detailPage.getFilter().click();
         * 
         * detailPage.getPointType().removeItemByIndex(0);
         * detailPage.getFilter().click();
         * 
         * List<String> pointType = new ArrayList<>(List.of("Analog", "Calc Analog", "Calc Status", "Demand Accumulator",
         * "Pulse Accumulator", "Status"));
         * 
         * List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypeList).containsExactlyElementsOf(pointType);
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_FilterMultiplePoints_Correct() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * 
         * HashMap<String, Pair<JSONObject, JSONObject>> pair =
         * VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();
         * Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");
         * 
         * JSONObject virtDevResponse = virtualDevice.getValue1();
         * 
         * virtualDeviceId = virtDevResponse.getInt("id");
         * 
         * detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
         * 
         * detailPage.getPointType().selectItemByText("Calc Analog");
         * detailPage.getPointType().selectItemByText("Status");
         * 
         * detailPage.getFilter().click();
         * 
         * String[] pointType = {"Calc Analog", "Status"};
         * 
         * List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypeList).containsOnly(pointType);
         */
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

        Map<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAnalogPoint();
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

        Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceOnlyRequiredFields();

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
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        //

        /*
         * setRefreshPage(false);
         * 
         * //
         * ======================================================================================================================
         * 
         * TODO
         * 
         * Please review the list tests that were done in load group, we should be following what was done there. We should be
         * creating points with specific names to make sure that the sorting is working as we expect it to. The creation of these
         * should be done in the BeforeClass also so that they can be used for all sorting test methods and we do not create them
         * in each test.
         * 
         * //
         * ======================================================================================================================
         * 
         * List<String> expectedLabels = new ArrayList<>(List.of("Point Name", "Attribute", "", "Value/State", "Date/Time",
         * "Point Type", "Point Offset"));
         * 
         * List<String> actualLabels = detailPage.getVirtualDevicePointsPanel().getTable().getListTableHeaders();
         * 
         * assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DevPtsSortPointNamesAsc_Correctly() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * Collections.sort(pointNames, String.CASE_INSENSITIVE_ORDER);
         * 
         * detailPage.getPointsPointsTableHeader().selectColumnNameByLink(1);
         * //
         * ======================================================================================================================
         * 
         * TODO
         * 
         * This code will need to be updated to follow the code for sorting in the Attribute Assignment table. Just clicking the
         * column header is not enough to get the sorting to work correctly. Name sorting is a little easier than the sorting on
         * all the other columns Implies for below code too
         * 
         * //
         * ======================================================================================================================
         * 
         * List<String> pointNamesList = detailPage.getTable().getDataRowsTextByCellIndex(1);
         * 
         * assertThat(pointNames).isEqualTo(pointNamesList);
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_DevPtsSortPointNamesDesc_Correctly() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * Collections.sort(pointNames, String.CASE_INSENSITIVE_ORDER);
         * Collections.reverse(pointNames);
         * 
         * detailPage.getPointsPointsTableHeader().selectColumnNameByLink(1);
         * 
         * List<String> namesList = detailPage.getTable().getDataRowsTextByCellIndex(1);
         * 
         * assertThat(pointNames).isEqualTo(namesList);
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortPointTypeAsc_Correctly() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * Collections.sort(pointTypes, String.CASE_INSENSITIVE_ORDER);
         * 
         * detailPage.getPointsPointsTableHeader().selectColumnNameByLink(6);
         * 
         * List<String> pointTypesList = detailPage.getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypes).isEqualTo(pointTypesList);
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortPointTypeDesc_Correctly() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * Collections.sort(pointTypes, String.CASE_INSENSITIVE_ORDER);
         * Collections.reverse(pointTypes);
         * 
         * detailPage.getPointsPointsTableHeader().selectColumnNameByLink(6);
         * 
         * List<String> pointTypesList = detailPage.getTable().getDataRowsTextByCellIndex(6);
         * 
         * assertThat(pointTypes).isEqualTo(pointTypesList);
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortPointOffsetAsc_Correctly() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * Collections.sort(pointOffsets, String.CASE_INSENSITIVE_ORDER);
         * 
         * detailPage.getPointsPointsTableHeader().selectColumnNameByLink(7);
         * 
         * List<String> pointOffsetList = detailPage.getTable().getDataRowsTextByCellIndex(7);
         * 
         * assertThat(pointOffsets).isEqualTo(pointOffsetList);
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortPointOffsetDesc_Correctly() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * Collections.sort(pointOffsets, String.CASE_INSENSITIVE_ORDER);
         * Collections.reverse(pointOffsets);
         * 
         * detailPage.getPointsPointsTableHeader().selectColumnNameByLink(7);
         * 
         * List<String> pointOffsetList = detailPage.getTable().getDataRowsTextByCellIndex(7);
         * 
         * assertThat(pointOffsets).isEqualTo(pointOffsetList);
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortStatusAsc_Correctly() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * Collections.sort(pointOffsets, String.CASE_INSENSITIVE_ORDER);
         * 
         * detailPage.getPointsPointsTableHeader().selectColumnNameByLink(2);
         * 
         * List<String> pointOffsetList = detailPage.getTable().getDataRowsTextByCellIndex(7);
         * 
         * assertThat(pointOffsets).isEqualTo(pointStatus);
         */
    }

    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Features.VIRTUAL_DEVICES, TestConstants.Features.ASSETS })
    public void virtualDeviceDetails_SortStatusDesc_Correctly() {
        throw new SkipException("Development Defect: YUK-23130");

        // ======================================================================================================================
        // As YUK-23130 gets fixed, remove above exception and uncomment below test code
        // ======================================================================================================================
        /*
         * setRefreshPage(true);
         * Collections.sort(pointOffsets, String.CASE_INSENSITIVE_ORDER);
         * Collections.reverse(pointOffsets);
         * 
         * detailPage.getPointsPointsTableHeader().selectColumnNameByLink(2);
         * 
         * List<String> pointOffsetList = detailPage.getTable().getDataRowsTextByCellIndex(7);
         * 
         * assertThat(pointOffsets).isEqualTo(pointStatus);
         */
    }
}
