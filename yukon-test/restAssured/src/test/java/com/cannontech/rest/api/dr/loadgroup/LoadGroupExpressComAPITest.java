package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockAddressUsage;
import com.cannontech.rest.api.loadgroup.request.MockControlPriority;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupExpresscom;
import com.cannontech.rest.api.loadgroup.request.MockLoads;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class LoadGroupExpressComAPITest {

    MockLoadGroupExpresscom loadGroup = null;

    @BeforeClass
    public void setUp() {
        loadGroup = (MockLoadGroupExpresscom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_EXPRESSCOMM);
    }

    /**
     * This test case validates creation of Expresscomm load group with default values
     */
    @Test
    public void loadGroupExpresscom_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", groupId != null);
        Log.endTestCase("loadGroupExpresscom_01_Create");

    }

    /**
     * This test case validates retrieval(Get) of Expresscomm load group and validates response
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_01_Create" })
    public void loadGroupExpresscom_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_02_Get");
        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

        Log.info("GroupId of LmGroupExpresscomm created is : " + groupId);

        ExtractableResponse<?> response = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        MockLoadGroupExpresscom loadGroupExpresscomResponse = response.as(MockLoadGroupExpresscom.class);
        context.setAttribute("Expresscomm_GrpName", loadGroupExpresscomResponse.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(loadGroupExpresscomResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == loadGroupExpresscomResponse.getType());
        assertTrue("routeId Should be : " + loadGroup.getRouteId(),
                loadGroup.getRouteId().intValue() == loadGroupExpresscomResponse.getRouteId().intValue());

        Log.endTestCase("loadGroupExpresscom_02_Get");

    }

    /**
     * This test case validates updation of Expresscomm load group and validates response with updated values
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_01_Create" })
    public void loadGroupExpresscom_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_03_Update");

        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        String name = "Auto_ExpresscommGroup_Update";
        context.setAttribute("expresscom_UpdateGrpName", name);

        loadGroup.setName(name);
        loadGroup.setKWCapacity(785.0);
        Log.info("Updated Load Group is :" + loadGroup);

        ExtractableResponse<?> response = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        ExtractableResponse<?> getupdatedLoadGroupExpresscomResponse = ApiCallHelper.get("getloadgroup", groupId);

        MockLoadGroupExpresscom updatedLoadGroupExpresscomResponse = getupdatedLoadGroupExpresscomResponse
                .as(MockLoadGroupExpresscom.class);
        assertTrue("Name Should be : " + loadGroup.getName(),
                loadGroup.getName().equals(updatedLoadGroupExpresscomResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(),
                loadGroup.getType() == updatedLoadGroupExpresscomResponse.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(),
                loadGroup.getKWCapacity().equals(updatedLoadGroupExpresscomResponse.getKWCapacity()));
        Log.endTestCase("loadGroupExpresscom_04_Update");

    }

    /**
     * This test case validates copy of Expresscom load group
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_01_Create" })
    public void loadGroupExpresscom_04_Copy(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_EXPRESSCOMM)).build();

        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        String copyGroupId = copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", copyGroupId != null);
        context.setAttribute("expresscom_CopyGrpId", copyGroupId);
        context.setAttribute("expresscom_CopyGrpName", loadGroupCopy.getName());
        Log.endTestCase("loadGroupExpresscom_04_Copy");
    }

    /**
     * This test case validates deletion of Expresscomm load group
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_02_Get" })
    public void loadGroupExpresscom_05_Delete(ITestContext context) {

        String expectedMessage = "Id not found";
        String grpToDelete = "expresscom_UpdateGrpName";
        Log.startTestCase("loadGroupExpresscom_05_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute(grpToDelete).toString()).build();
        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 400", getDeletedResponse.statusCode() == 400);

        MockApiError error = getDeletedResponse.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));

        // Delete copy Load group
        lmDeleteObject = MockLMDto.builder().name(context.getAttribute("expresscom_CopyGrpName").toString()).build();
        ExtractableResponse<?> deleteCopyResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute("expresscom_CopyGrpId").toString());
        assertTrue("Status code should be 200", deleteCopyResponse.statusCode() == 200);
        Log.endTestCase("loadGroupExpresscom_05_Delete");
    }

    /**
     * This test case validates negative scenarios of Emetcon load group with different input data provided in
     * DataProviderClass
     */
    @Test(dataProvider = "ExpresscomAddressData", dependsOnMethods = "loadGroupExpresscom_01_Create")
    public void loadGroupExpresscom_06_PhysicalAddressValidation(String spid, String geoId, String subId, String zip, String user,
            String expectedErrorMsg,
            Integer expectedStatusCode) {

        Log.startTestCase("loadGroupExpresscom_06_PhysicalAddressValidation");

        List<MockAddressUsage> addressUsage = new ArrayList<>();
        addressUsage.add(MockAddressUsage.GEO);
        addressUsage.add(MockAddressUsage.SUBSTATION);
        addressUsage.add(MockAddressUsage.USER);
        addressUsage.add(MockAddressUsage.PROGRAM);
        addressUsage.add(MockAddressUsage.SPLINTER);
        addressUsage.add(MockAddressUsage.LOAD);
        addressUsage.add(MockAddressUsage.ZIP);
        addressUsage.add(MockAddressUsage.FEEDER);

        List<MockLoads> relayUsage = new ArrayList<>();
        relayUsage.add(MockLoads.Load_1);

        MockLoadGroupExpresscom loadGroup = MockLoadGroupExpresscom.builder()
                .name("Test_ExpressCom_LoadGroup")
                .type(MockPaoType.LM_GROUP_EXPRESSCOMM)
                .routeId(47)
                .disableControl(false)
                .disableGroup(false)
                .feeder("1000000000000000")
                .serviceProvider(100)
                .geo(223)
                .zip(3334)
                .kWCapacity(0.0)
                .addressUsage(addressUsage)
                .relayUsage(relayUsage)
                .serialNumber("1245")
                .program(12)
                .protocolPriority(MockControlPriority.DEFAULT)
                .build();

        loadGroup.setSplinter(Integer.valueOf(spid));
        loadGroup.setGeo(Integer.valueOf(geoId));
        loadGroup.setZip(Integer.valueOf(zip));
        loadGroup.setUser(Integer.valueOf(user));
        loadGroup.setSubstation(Integer.valueOf(subId));

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        Integer statusCode = response.statusCode();
        assertTrue("Status code should be " + expectedStatusCode, expectedStatusCode.equals(statusCode));

        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedErrorMsg.equals(error.getFieldErrors().get(0).getCode()));

        Log.endTestCase("loadGroupExpresscom_06_PhysicalAddressValidation");
    }

    /**
     * Negative validation when Load Group is copied with invalid Route Id
     */
    @Test(dependsOnMethods = "loadGroupExpresscom_01_Create")
    public void loadGroupExpresscom_07_CopyWithInvalidRouteId(ITestContext context) {

        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_EXPRESSCOMM)).build();
        loadGroupCopy.setRouteId(LoadGroupHelper.invalidRouteId);
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup", loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(copyResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(copyResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(copyResponse, "routeId", "Route Id does not exist."),
                "Expected code in response is not correct");
    }

    /**
     * DataProvider provides data to test method in the form of object array Data provided in test data sheet - col1 :
     * serviceProviderId col2 : geoId col3 : substationId col4 : feeder col5 : zip col6 : user col7 : Expected field
     * errors code in response col8 : Expected response code
     */
    @DataProvider(name = "ExpresscomAddressData")
    public Object[][] getExpresscomAddressData(ITestContext context) {

        return new Object[][] { { "65535", "22", "36", "16777214", "36", "Must be between 1 and 99.", 422 },
                { "0", "22", "36", "16777214", "36", "Must be between 1 and 99.", 422 },
                { "22", "65535", "36", "16777214", "36", "Must be between 1 and 65,534.", 422 },
                { "22", "0", "36", "16777214", "36", "Must be between 1 and 65,534.", 422 },
                { "22", "22", "0", "16777214", "36", "Must be between 1 and 65,534.", 422 },
                { "22", "22", "65535", "16777214", "36", "Must be between 1 and 65,534.", 422 },
                { "22", "22", "65534", "0", "36", "Must be between 1 and 16,777,214.", 422 },
                { "22", "22", "65534", "16777215", "36", "Must be between 1 and 16,777,214.", 422 },
                { "22", "22", "65534", "16777214", "0", "Must be between 1 and 65,534.", 422 },
                { "22", "22", "65534", "16777214", "65535", "Must be between 1 and 65,534.", 422 } };
    }

}
