package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.utilities.Log;
import io.restassured.response.ExtractableResponse;

public class LoadGroupMeterDisconnectAPITest {
    MockLoadGroupBase loadGroup = null;

    @BeforeClass
    public void setUp() {
        loadGroup = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_METER_DISCONNECT);
    }

    @Test
    public void loadGroupMeterDisconnect_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupMeterDisconnect_01_Create");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, groupId);

        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", groupId != null);

        Log.endTestCase("loadGroupMeterDisconnect_01_Create");
    }

    @Test(dependsOnMethods = { "loadGroupMeterDisconnect_01_Create" })
    public void loadGroupMeterDisconnect_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupMeterDisconnect_02_Get");
        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

        Log.info("Group Id of LmGroupMeterDisconnect created is : " + groupId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupBase meterDisconnectGroup = getResponse.as(MockLoadGroupBase.class);
        context.setAttribute("MR_GrpName", meterDisconnectGroup.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(meterDisconnectGroup.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == meterDisconnectGroup.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(meterDisconnectGroup.getKWCapacity()));

        Boolean disableGroup = meterDisconnectGroup.isDisableGroup();
        assertTrue("Group Should be disabled : ", !disableGroup);

        Boolean disableControl = meterDisconnectGroup.isDisableControl();
        assertTrue("Control Should be disabled : ", !disableControl);
        Log.endTestCase("loadGroupMeterDisconnect_02_Get");
    }

    @Test(dependsOnMethods = { "loadGroupMeterDisconnect_01_Create" })
    public void loadGroupMeterDisconnect_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupMeterDisconnect_03_Update");

        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        loadGroup.setKWCapacity(888.766);
        loadGroup.setName("Meter_Disconnect_Update_Test");

        Log.info("Updated Load Group is :" + loadGroup.toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        context.setAttribute("MR_GrpName", loadGroup.getName());
        ExtractableResponse<?> getUpdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        MockLoadGroupBase updatedMeterDisconnectGroup = getUpdatedResponse.as(MockLoadGroupBase.class);
        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(updatedMeterDisconnectGroup.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == updatedMeterDisconnectGroup.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(updatedMeterDisconnectGroup.getKWCapacity()));

        Log.endTestCase("loadGroupMeterDisconnect_03_Update");
    }

    /**
     * This test case validates copy of Meter Disconnect load group
     */
    @Test(dependsOnMethods = { "loadGroupMeterDisconnect_01_Create" })
    public void loadGroupMeterDisconnect_04_Copy(ITestContext context) {

        Log.startTestCase("loadGroupMeterDisconnect_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                                                           .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_METER_DISCONNECT))
                                                           .build();

        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                                                                 loadGroupCopy,
                                                                 context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        String copyPaoId = copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        assertTrue("Group Id should not be Null", copyPaoId != null);
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
        Log.endTestCase("loadGroupMeterDisconnect_04_Copy");
    }

    /**
     * This test case validates deletion of Meter Disconnect load group
     */
    @Test(dependsOnMethods = { "loadGroupMeterDisconnect_02_Get" })
    public void loadGroupMeterDisconnect_05_Delete(ITestContext context) {
        Log.startTestCase("loadGroupMeterDisconnect_05_Delete");
        String expectedMessage = "Id not found";
        String grpToDelete = "MR_GrpName";
        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute(grpToDelete).toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup",
                                                               lmDeleteObject,
                                                               context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedResponse = ApiCallHelper.get("getloadgroup", context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 400", getDeletedResponse.statusCode() == 400);
        MockApiError error = getDeletedResponse.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));
        Log.startTestCase("loadGroupMeterDisconnect_05_Delete");
    }
}
