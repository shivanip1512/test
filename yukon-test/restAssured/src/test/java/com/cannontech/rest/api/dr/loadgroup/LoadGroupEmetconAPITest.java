package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockEmetconAddressUsage;
import com.cannontech.rest.api.loadgroup.request.MockEmetconRelayUsage;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupEmetcon;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LoadGroupEmetconAPITest {

    MockLoadGroupEmetcon loadGroup = null;

    @BeforeClass
    public void setUp() {
        loadGroup = (MockLoadGroupEmetcon) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_EMETCON);
    }

    /**
     * This test case validates creation of Emetcon load group with default values
     */
    @Test
    public void loadGroupEmetcon_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupEmetcon_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", groupId != null);
        Log.endTestCase("loadGroupEmetcon_01_Create");

    }

    /**
     * This test case validates retrieval(Get) of Emetcon load group and validates response
     */
    @Test(dependsOnMethods = { "loadGroupEmetcon_01_Create" })
    public void loadGroupEmetcon_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupEmetcon_02_Get");
        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

        Log.info("GroupId of LmGroupEmetcon created is : " + groupId);

        ExtractableResponse<?> response = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        MockLoadGroupEmetcon loadGroupEmetcon = response.as(MockLoadGroupEmetcon.class);
        context.setAttribute("Emetcon_GrpName", loadGroupEmetcon.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(loadGroupEmetcon.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == loadGroupEmetcon.getType());
        assertTrue("routeId Should be : " + loadGroup.getRouteId(), loadGroup.getRouteId() == loadGroup.getRouteId());

        Log.endTestCase("loadGroupEmetcon_02_Get");

    }

    /**
     * This test case validates updation of Emetcon load group and validates response with updated values
     */
    @Test(dependsOnMethods = { "loadGroupEmetcon_01_Create" })
    public void loadGroupEmetcon_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupEmetcon_03_Update");

        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

        String name = "Auto_EmetconGroup_Update2";
        context.setAttribute("emetcon_UpdateGrpName", name);

        loadGroup.setRelayUsage(MockEmetconRelayUsage.RELAY_B);
        loadGroup.setName(name);

        Log.info("Updated Load Group is :" + loadGroup);

        ExtractableResponse<?> response = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        MockLoadGroupEmetcon updatedLoadGroupEmetcon = getupdatedResponse.as(MockLoadGroupEmetcon.class);
        assertTrue("Name Should be : " + name, name.equals(updatedLoadGroupEmetcon.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == updatedLoadGroupEmetcon.getType());
        assertTrue("relayUsage Should be : " + MockEmetconRelayUsage.RELAY_B,
                MockEmetconRelayUsage.RELAY_B == updatedLoadGroupEmetcon.getRelayUsage());
        Log.endTestCase("loadGroupEmetcon_04_Update");

    }

    /**
     * This test case validates copy of Emetcon load group
     */
    @Test(dependsOnMethods = { "loadGroupEmetcon_01_Create" })
    public void loadGroupEmetcon_04_Copy(ITestContext context) {

        Log.startTestCase("loadGroupEmetcon_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_EMETCON)).build();

        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        String copyGroupId = copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", copyGroupId != null);
        context.setAttribute("Emetcon_CopyGrpId", copyGroupId);
        context.setAttribute("Emetcon_CopyGrpName", loadGroupCopy.getName());
        Log.endTestCase("loadGroupEmetcon_04_Copy");
    }

    /**
     * This test case validates negative scenarios of Emetcon load group with different input data provided in
     * DataProviderClass
     */
    @Test(dataProvider = "EmetconAddressData", dependsOnMethods = "loadGroupEmetcon_01_Create")
    public void loadGroupEmetcon_05_AddressValidation(String goldAddress, String silverAddress, String expectedErrorMsg,
            Integer expectedStatusCode) {

        Log.startTestCase("loadGroupEmetcon_05_AddressValidation");

        loadGroup.setGoldAddress(Integer.valueOf(goldAddress));
        loadGroup.setSilverAddress(Integer.valueOf(silverAddress));
        loadGroup.setAddressUsage(MockEmetconAddressUsage.GOLD);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        Integer statusCode = response.statusCode();
        assertTrue("Status code should be " + expectedStatusCode, expectedStatusCode.equals(statusCode));

        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedErrorMsg.equals(error.getFieldErrors().get(0).getCode()));

        Log.endTestCase("loadGroupEmetcon_05_AddressValidation");
    }

    /**
     * This test case validates deletion of Emetcon load group
     */
    @Test(dependsOnMethods = { "loadGroupEmetcon_03_Update" })
    public void loadGroupEmetcon_06_Delete(ITestContext context) {

        String expectedMessage = "Id not found";
        String grpToDelete = "emetcon_UpdateGrpName";
        Log.startTestCase("loadGroupEmetcon_06_Delete");

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
        lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Emetcon_CopyGrpName").toString()).build();
        ExtractableResponse<?> deleteCopyResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute("Emetcon_CopyGrpId").toString());
        assertTrue("Status code should be 200", deleteCopyResponse.statusCode() == 200);

        Log.endTestCase("loadGroupEmetcon_06_Delete");
    }

    /**
     * DataProvider provides data to test method in the form of object array Data provided in test data sheet - col1 :
     * goldAddress col2 : silverAddress col3 : expectedErrorMsg col4 : expectedStatusCode
     */
    @DataProvider(name = "EmetconAddressData")
    public Object[][] getEmetconAddressData(ITestContext context) {

        return new Object[][] { { "5", "22", "Must be between 0 and 4.", 422 },
                { "0", "0", "Gold Address must be between 1 and 4 when Gold is selected for Address to Use.", 422 },
                { "-1", "22", "Must be between 0 and 4.", 422 }, { "3", "61", "Must be between 0 and 60.", 422 },
                { "3", "-1", "Must be between 0 and 60.", 422 } };
    }

}
