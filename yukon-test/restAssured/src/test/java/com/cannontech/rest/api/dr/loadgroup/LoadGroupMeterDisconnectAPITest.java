package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LoadGroupMeterDisconnectAPITest {
    MockLoadGroupBase loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        loadGroup = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_METER_DISCONNECT);
    }

    @Test(priority = 0)
    public void Test01_LmGroupMeterDisconnect_Create(ITestContext context) {
        Log.startTestCase("LmGroupMeterDisconnect_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, groupId);

        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", groupId != null);

        Log.endTestCase("LmGroupMeterDisconnect_Create");
    }

    @Test(priority = 1)
    public void Test02_LmGroupMeterDisconnect_Get(ITestContext context) {
        Log.startTestCase("Test02_LmGroupMeterDisconnect_Get");
        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

        Log.info("Load Group Id of LmGroupMeterDisconnect created is : " + groupId);

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
        Log.endTestCase("Test02_LmGroupMeterDisconnect_Get");
    }

    @Test(priority = 2)
    public void Test03_LmGroupMeterDisconnect_Update(ITestContext context) {
        Log.startTestCase("Test03_LmGroupMeterDisconnect_Update");

        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        loadGroup.setKWCapacity(888.766);
        loadGroup.setName("Meter_Disconnect_Update_Test");
        context.setAttribute("MR_GrpName", loadGroup.getName());

        Log.info("Updated Load Group is :" + loadGroup.toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getUpdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        MockLoadGroupBase updatedMeterDisconnectGroup = getUpdatedResponse.as(MockLoadGroupBase.class);
        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(updatedMeterDisconnectGroup.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == updatedMeterDisconnectGroup.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(updatedMeterDisconnectGroup.getKWCapacity()));

        Log.endTestCase("Test03_LmGroupMeterDisconnect_Update");

    }
}
