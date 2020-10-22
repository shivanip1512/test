package com.eaton.tests.assets.virtualdevices;

import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.HashMap;
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
    
    // ===================================================================================================
    // Below code Commented due to YUK-23130
    // ===================================================================================================
    // private List<String> pointNames;
    // private List<String> pointTypes;
    // private List<String> pointOffsets;
    
    @BeforeClass(alwaysRun = true)
    public void beforeClass() {       
        driverExt = getDriverExt();
        setRefreshPage(false);
             
        Boolean enable;
        
        HashMap<String, Pair<JSONObject, JSONObject>> pair  = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();

        Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	
    	JSONObject response = virtualDevice.getValue1();    
    	
    	virtualDeviceId = response.getInt("id");
    	virtualDeviceName = response.getString("name");
        enable = response.getBoolean("enable");
        
        virtualDeviceStatus = (enable.equals(true)) ? "Enabled" : "Disabled";
        
        navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
        // ===================================================================================================
        // Below code Commented due to YUK-23130
        // ===================================================================================================
        // pointNames = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(1);
        // pointTypes = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
        // pointOffsets = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(7);
            
    }
    
    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
    	if(getRefreshPage()) {
            refreshPage(detailPage);    
        }
        setRefreshPage(false);
    }
	
    @Test(groups = { TestConstants.Priority.CRITICAL, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_PageTitle_Correct() {
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
    	
    	String expectedTitle = virtualDeviceName;
        
        String actualPageTitle = detailPage.getPageTitle();
        
        assertThat(expectedTitle).isEqualTo(actualPageTitle);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_InfoSection_Displayed() {
    	setRefreshPage(true);
    	
    	String expectedPanelText = "Virtual Device Information";

        String actualPanelText = detailPage.getVirtualDeviceInfoPanel().getPanelName();

        assertThat(actualPanelText).isEqualTo(expectedPanelText);
    }
    
    @Test(groups = { TestConstants.Priority.MEDIUM, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_DevicePoints_Displayed() {
    	setRefreshPage(true);
    	
    	String expectedPanelText = "Device Points";

        String actualPanelText = detailPage.getVirtualDevicePointsPanel().getPanelName();

        assertThat(actualPanelText).isEqualTo(expectedPanelText);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})    
    public void  virtualDeviceDetails_InfoFieldLabels_Correct() {
    	setRefreshPage(true);
    	
    	SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelCount()).isEqualTo(2);
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelByRow(0)).isEqualTo("Name:");
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getLabelByRow(1)).isEqualTo("Status:");
        softly.assertAll();

    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})    
    public void  virtualDeviceDetails_InfoFieldValues_Correct() {
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
    	
    	SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueCount()).isEqualTo(2);
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueByRow(0)).isEqualTo(virtualDeviceName);
        softly.assertThat(detailPage.getVirtualDeviceInfoPanel().getNameStatusTable().getValueByRow(1)).isEqualTo(virtualDeviceStatus);
        softly.assertAll();
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})    
    public void  virtualDeviceDetails_InfoEdit_OpensCorrectModal() {
    	setRefreshPage(true);
	  
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
    	final String EXP_MODAL_TITLE = "Edit " + virtualDeviceName;
    	 
    	EditVirtualDeviceModal editModal = detailPage.showAndWaitEditVirtualDeviceModal();
    	
    	String title = editModal.getModalTitle();

        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    	
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS}) 
    public void  virtualDeviceDetails_Create_OpensCorrectModal() {
    	setRefreshPage(true);
    	
    	final String EXP_MODAL_TITLE = "Create Virtual Device";
    	waitForLoadingSpinner();
    	CreateVirtualDeviceModal createModal = detailPage.showAndWaitCreateVirtualDeviceModal();
    	
    	String title = createModal.getModalTitle();

        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_OtherActionsUrl_Correct() {
    	setRefreshPage(true);
	  
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
    	
    	final String otherActionsUrl = Urls.OTHER_ACTIONS + virtualDeviceId;
    	
    	detailPage.getActionBtn().clickAndSelectOptionByText("Other Actions");
    	
    	boolean loaded = waitForUrlToLoad(otherActionsUrl, Optional.empty());
	
    	assertThat(loaded).isTrue();
    	
    	//Validation for response code as 200
        ExtractableResponse<?> response = ApiCallHelper.get(otherActionsUrl);
        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_Delete_OpensCorrectModal() {
		Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceOnlyRequiredFields();
		  
		JSONObject response = pair.getValue1();
		 
		Integer id = response.getInt("id");
		 
		navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id); 
    	
        String expectedModalTitle = "Confirm Delete";

        ConfirmModal deleteConfirmModal = detailPage.showAndWaitDeleteVirtualDeviceModal();

        String actualModalTitle = deleteConfirmModal.getModalTitle();

        assertThat(actualModalTitle).isEqualTo(expectedModalTitle);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_CreateAnalogPointUrl_Correct() {
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAnalogPoint();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	Pair<JSONObject, JSONObject> anlaogPoint = pair.get("AnalogPoint");        
    	
    	JSONObject virtualDeviceResponse = virtualDevice.getValue1();    
    	JSONObject analogPointResponse = anlaogPoint.getValue1();
    	
    	Integer id = virtualDeviceResponse.getInt("id");
    	Integer pointId =  analogPointResponse.getInt("pointId");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);
        
    	final String pointUrl = Urls.Tools.POINT + pointId;
    	
    	detailPage.getPointsTableRow(1).selectCellByLink();

    	boolean loaded = waitForUrlToLoad(pointUrl, Optional.empty());
	
    	assertThat(loaded).isTrue();
    	
    	//Validation for response code as 200
        ExtractableResponse<?> response = ApiCallHelper.get(pointUrl);
        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_CreateCalcAnalogPointUrl_Correct() {
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithCalcAnalogPoint();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	Pair<JSONObject, JSONObject> calcAnalogPoint = pair.get("CalcAnalogPoint");        
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	JSONObject calcAnlgPtResponse = calcAnalogPoint.getValue1();
    	
    	Integer id = virtDevResponse.getInt("id");
    	Integer pointId =  calcAnlgPtResponse.getInt("pointId");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);
        
    	final String pointUrl = Urls.Tools.POINT + pointId;
    	
    	detailPage.getPointsTableRow(1).selectCellByLink();

    	boolean loaded = waitForUrlToLoad(pointUrl, Optional.empty());
	
    	assertThat(loaded).isTrue();
    	
    	//Validation for response code as 200
        ExtractableResponse<?> response = ApiCallHelper.get(pointUrl);
        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_CreateCalcStatusPointUrl_Correct() {
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithCalcStatusPoint();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	Pair<JSONObject, JSONObject> clacStatusPoint = pair.get("CalcStatusPoint");        
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	JSONObject calcStsPtResponse = clacStatusPoint.getValue1();
    	
    	Integer id = virtDevResponse.getInt("id");
    	Integer pointId =  calcStsPtResponse.getInt("pointId");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);
        
    	final String pointUrl = Urls.Tools.POINT + pointId;
    	
    	detailPage.getPointsTableRow(1).selectCellByLink();

    	boolean loaded = waitForUrlToLoad(pointUrl, Optional.empty());
	
    	assertThat(loaded).isTrue();
    	
    	//Validation for response code as 200
        ExtractableResponse<?> response = ApiCallHelper.get(pointUrl);
        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_CreateDemandAccPointUrl_Correct() {
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithDemandAccumulatorPoint();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	Pair<JSONObject, JSONObject> demandAccPoint = pair.get("DemandAccumulatorPoint");        
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	JSONObject dmdAccPtResponse = demandAccPoint.getValue1();
    	
    	Integer id = virtDevResponse.getInt("id");
    	Integer pointId =  dmdAccPtResponse.getInt("pointId");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);
        
    	final String pointUrl = Urls.Tools.POINT + pointId;
    	
    	detailPage.getPointsTableRow(1).selectCellByLink();

    	boolean loaded = waitForUrlToLoad(pointUrl, Optional.empty());
	
    	assertThat(loaded).isTrue();
    	
    	//Validation for response code as 200
        ExtractableResponse<?> response = ApiCallHelper.get(pointUrl);
        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_CreatePulseAccPointUrl_Correct() {
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithPulseAccumulatorPoint();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	Pair<JSONObject, JSONObject> pulseAccPoint = pair.get("PulseAccumulatorPoint");        
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	JSONObject pulsAccPtResponse = pulseAccPoint.getValue1();
    	
    	Integer id = virtDevResponse.getInt("id");
    	Integer pointId =  pulsAccPtResponse.getInt("pointId");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);
        
    	final String pointUrl = Urls.Tools.POINT + pointId;
    	
    	detailPage.getPointsTableRow(1).selectCellByLink();

    	boolean loaded = waitForUrlToLoad(pointUrl, Optional.empty());
	
    	assertThat(loaded).isTrue();
    	
    	//Validation for response code as 200
        ExtractableResponse<?> response = ApiCallHelper.get(pointUrl);
        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_CreateStatusPointUrl_Correct() {
    	setRefreshPage(true);
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithStatusPoint();
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	Pair<JSONObject, JSONObject> statusPoint = pair.get("StatusPoint");        
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	JSONObject stsPtResponse = statusPoint.getValue1();
    	
    	Integer id = virtDevResponse.getInt("id");
    	Integer pointId =  stsPtResponse.getInt("pointId");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);
        
    	final String pointUrl = Urls.Tools.POINT + pointId;
    	
    	detailPage.getPointsTableRow(1).selectCellByLink();

    	boolean loaded = waitForUrlToLoad(pointUrl, Optional.empty());
	
    	assertThat(loaded).isTrue();
    	
    	//Validation for response code as 200
        ExtractableResponse<?> response = ApiCallHelper.get(pointUrl);
        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_DevPtsPointNameUrl_Correct() throws IOException {
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAnalogPoint();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	Pair<JSONObject, JSONObject> analogPoint = pair.get("AnalogPoint");        
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	JSONObject stsPtResponse = analogPoint.getValue1();
    	
    	Integer id = virtDevResponse.getInt("id");
    	Integer pointId =  stsPtResponse.getInt("pointId");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);
        
    	final String EXP_POINT_URL = getBaseUrl() + Urls.Tools.POINT + pointId;
    	
    	String pointUrl = detailPage.getPointsTableRow(1).getCellLinkByIndex(0);

    	assertThat(EXP_POINT_URL).isEqualTo(pointUrl);
    	
    	//Validation for response code as 200
        ExtractableResponse<?> response = ApiCallHelper.get(pointUrl);
        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_DevPtsDateTime_OpensCorrectModal() {
    	final String EXP_MODAL_TITLE = "Recent Archived Readings";
    	
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAnalogPoint();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");     
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();  
    	
    	Integer id = virtDevResponse.getInt("id");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);

        RecentArchievedRadingsModal rcntArchReadingsModal = detailPage.showAndWaitRecentArchievedReadingsModal("Recent Archived Readings", 5);
        
        String title = rcntArchReadingsModal.getModalTitle();
        
        assertThat(EXP_MODAL_TITLE).isEqualTo(title);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_FilterPointTypeByStatus_Correct() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
    	
    	
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	
    	virtualDeviceId = virtDevResponse.getInt("id");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
    	detailPage.getPointType().selectItemByText("Status");
    	detailPage.getFilter().click();
    	
    	List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
    	
    	assertThat(pointTypeList).containsOnly("Status");   	
    	*/
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_FilterPointTypeByAnalogPt_Correct() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
    	
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	Pair<JSONObject, JSONObject> statusPoint = pair.get("AnalogPoint");        
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	JSONObject stsPtResponse = statusPoint.getValue1();
    	
    	virtualDeviceId = virtDevResponse.getInt("id");
    	
    	Integer pointId =  stsPtResponse.getInt("pointId");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
    	detailPage.getPointType().selectItemByText("Analog");
    	detailPage.getFilter().click();
    	
    	List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
    	
    	assertThat(pointTypeList).containsOnly("Analog");
    	*/
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_FilterPointTypeByCalcStatusPt_Correct() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
    	
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	
    	virtualDeviceId = virtDevResponse.getInt("id");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
    	detailPage.getPointType().selectItemByText("Calc Status");
    	detailPage.getFilter().click();
    	
    	List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
    	
    	assertThat(pointTypeList).containsOnly("Calc Status");
    	*/
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_FilterPointTypeByDemandAccPt_Correct() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
    	
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");            
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	
    	virtualDeviceId = virtDevResponse.getInt("id");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
    	detailPage.getPointType().selectItemByText("Demand Accumulator");
    	detailPage.getFilter().click();
    	
    	List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
    	
    	assertThat(pointTypeList).containsOnly("Demand Accumulator");
    	*/
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_FilterPointTypeByPulseAccPt_Correct() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
    	
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();
    	
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	
    	virtualDeviceId = virtDevResponse.getInt("id");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
    	detailPage.getPointType().selectItemByText("Pulse Accumulator");
    	detailPage.getFilter().click();
    	
    	List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
    	
    	assertThat(pointTypeList).containsOnly("Pulse Accumulator");
    	*/
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_FilterPointTypeByCalcAnalogPt_Correct() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
    	
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	virtualDeviceId = virtDevResponse.getInt("id");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
    	detailPage.getPointType().selectItemByText("Calc Analog");
    	detailPage.getFilter().click();
    	
    	List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
    	
    	assertThat(pointTypeList).containsOnly("Calc Analog");
    	*/
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_RemoveFilter_CorrectPointsDisplayed() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
    	
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	
    	virtualDeviceId = virtDevResponse.getInt("id");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
    	detailPage.getPointType().selectItemByText("Calc Analog");
    	detailPage.getFilter().click();
    	
    	detailPage.getPointType().clearSelectedItem();
    	detailPage.getFilter().click();
    	
    	List<String> pointType = new ArrayList<>(List.of("Analog", "Calc Analog", "Calc Status", "Demand Accumulator", "Pulse Accumulator", "Status"));
    	
    	List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
    	
    	assertThat(pointTypeList).containsExactlyElementsOf(pointType);
    	*/
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_FilterMultiplePoints_Correct() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
    	
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAllPoints();
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();    
    	
    	virtualDeviceId = virtDevResponse.getInt("id");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
    	detailPage.getPointType().selectItemByText("Calc Analog");
    	detailPage.getPointType().selectItemByText("Status");
    	
    	detailPage.getFilter().click();
    	
    	String[] pointType = {"Calc Analog", "Status"};
    	
    	List<String> pointTypeList = detailPage.getVirtualDevicePointsPanel().getTable().getDataRowsTextByCellIndex(6);
    	
    	assertThat(pointTypeList).containsOnly(pointType);
    	*/
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_DeleteModalMessage_Correct() {
    	Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceOnlyRequiredFields();     
    	
    	JSONObject response = pair.getValue1();    
    	
    	Integer id = response.getInt("id");
    	String name = response.getString("name");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
    	
    	final String EXP_DELETE_MODAL_MSG = "Are you sure you want to delete \"" + name + "\"?";
   	 
    	ConfirmModal deleteModal = detailPage.showAndWaitDeleteVirtualDeviceModal();
    	
    	String deleteMsg = deleteModal.getConfirmMsg();

        assertThat(EXP_DELETE_MODAL_MSG).isEqualTo(deleteMsg);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_DeleteWithPt_Success() {
    	HashMap<String, Pair<JSONObject, JSONObject>> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceWithAnalogPoint();
    	Pair<JSONObject, JSONObject> virtualDevice = pair.get("VirtualDevice");    
    	
    	JSONObject virtDevResponse = virtualDevice.getValue1();   
    	
    	Integer id = virtDevResponse.getInt("id");
    	String name = virtDevResponse.getString("name");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);
        
        final String EXP_MSG = name + " deleted successfully.";
      	 
    	ConfirmModal deleteModal = detailPage.showAndWaitDeleteVirtualDeviceModal();
    	deleteModal.clickOkAndWaitForModalToClose();
    	
    	String actualMsg = detailPage.getUserMessage();

        assertThat(EXP_MSG).isEqualTo(actualMsg);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_DeleteWithOutPt_Success() {
    	Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceOnlyRequiredFields();
    	
    	JSONObject virtDevResponse = pair.getValue1();   
    	
    	Integer id = virtDevResponse.getInt("id");
    	String name = virtDevResponse.getString("name");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + id);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, id);
        
        final String EXP_MSG = name + " deleted successfully.";
      	 
    	ConfirmModal deleteModal = detailPage.showAndWaitDeleteVirtualDeviceModal();
    	deleteModal.clickOkAndWaitForModalToClose();
    	
    	String actualMsg = detailPage.getUserMessage();

        assertThat(EXP_MSG).isEqualTo(actualMsg);
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDevicesDetails_DevPtsColumnHeaders_Correct() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
    	
    	Pair<JSONObject, JSONObject> pair = VirtualDeviceCreateService.buildAndCreateVirtualDeviceOnlyRequiredFields();
    	
    	JSONObject virtDevResponse = pair.getValue1();   
    	
    	virtualDeviceId = virtDevResponse.getInt("id");
    	virtualDeviceName = virtDevResponse.getString("name");
    	
    	navigate(Urls.Assets.VIRTUAl_DEVICE_DETAIL + virtualDeviceId);
        detailPage = new VirtualDevicesDetailPage(driverExt, Urls.Assets.VIRTUAl_DEVICE_DETAIL, virtualDeviceId);
        
        List<String> expectedLabels = new ArrayList<>(List.of("Point Name",	"Attribute", "", "Value/State",	"Date/Time", "Point Type", "Point Offset"));

        List<String> actualLabels = detailPage.getVirtualDevicePointsPanel().getTable().getListTableHeaders();

        assertThat(actualLabels).containsExactlyElementsOf(expectedLabels);
        */
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDevicesDetails_DevPtsSortPointNamesAsc_Correctly() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
        Collections.sort(pointNames, String.CASE_INSENSITIVE_ORDER);

        detailPage.getPointsPointsTableHeader().selectColumnNameByLink(1);

        List<String> pointNamesList = detailPage.getTable().getDataRowsTextByCellIndex(1);

        assertThat(pointNames).isEqualTo(pointNamesList);
        */
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDevicesDetails_DevPtsSortPointNamesDesc_Correctly() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
        Collections.sort(pointNames, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(pointNames);
        
        detailPage.getPointsPointsTableHeader().selectColumnNameByLink(1);

        List<String> namesList = detailPage.getTable().getDataRowsTextByCellIndex(1);

        assertThat(pointNames).isEqualTo(namesList);
    	*/
    }
       
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDeviceDetails_SortPointTypeAsc_Correctly() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
        Collections.sort(pointTypes, String.CASE_INSENSITIVE_ORDER);

        detailPage.getPointsPointsTableHeader().selectColumnNameByLink(6);

        List<String> pointTypesList = detailPage.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypes).isEqualTo(pointTypesList);
        */
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDevicesDetails_SortPointTypeDesc_Correctly() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
        Collections.sort(pointTypes, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(pointTypes);
        
        detailPage.getPointsPointsTableHeader().selectColumnNameByLink(6);

        List<String> pointTypesList = detailPage.getTable().getDataRowsTextByCellIndex(6);

        assertThat(pointTypes).isEqualTo(pointTypesList);
        */
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDevicesDetails_SortPointOffsetAsc_Correctly() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
        Collections.sort(pointOffsets, String.CASE_INSENSITIVE_ORDER);

        detailPage.getPointsPointsTableHeader().selectColumnNameByLink(7);

        List<String> pointOffsetList = detailPage.getTable().getDataRowsTextByCellIndex(7);

        assertThat(pointOffsets).isEqualTo(pointOffsetList);
        */
    }
    
    @Test(groups = { TestConstants.Priority.HIGH, TestConstants.Assets.VIRTUAL_DEVICES, TestConstants.Assets.ASSETS})
    public void  virtualDevicesDetails_SortPointOffsetDesc_Correctly() {
    	throw new SkipException("Development Defect: YUK-23130");
    	
    	// ======================================================================================================================
    	// As YUK-23130 gets fixed, remove above exception and uncomment below test code
    	// ======================================================================================================================
    	/*
    	setRefreshPage(true);
        Collections.sort(pointOffsets, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(pointOffsets);

        detailPage.getPointsPointsTableHeader().selectColumnNameByLink(7);

        List<String> pointOffsetList = detailPage.getTable().getDataRowsTextByCellIndex(7);

        assertThat(pointOffsets).isEqualTo(pointOffsetList);
        */
    }
    
}
