package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.JsonFileReader;
import com.cannontech.rest.api.utilities.Log;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupMeterDisconnectAPITest {
    private final static String meterDisconnectPaoTypeStr = "LM_GROUP_NEST";
    private final static String meterDisconnectpayloadfile = "loadgroup\\\\lmGroupMeterDisconnectCreate.json";

    @Test(priority = 0)
    public void Test01_LmGroupMeterDisconnect_Create(ITestContext context) {
        Log.startTestCase("LmGroupMeterDisconnect_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", meterDisconnectpayloadfile);
        String groupId = createResponse.path("groupId").toString();
        context.setAttribute("MeterDisconnectgroupId", groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("LmGroupMeterDisconnect_Create");
    }

    @Test(priority = 1)
    public void Test02_LmGroupMeterDisconnect_Get(ITestContext context) {
        Log.startTestCase("Test02_LmGroupMeterDisconnect_Get");
        String groupId = context.getAttribute("MeterDisconnectgroupId").toString();

        JSONObject jo = JsonFileReader.readJsonFileAsJSONObject(meterDisconnectpayloadfile);
        JsonPath jp = new JsonPath(jo.toJSONString());
        Float kWCapacity = jp.getFloat(meterDisconnectPaoTypeStr + ".kWCapacity");
        String name = jp.get(meterDisconnectPaoTypeStr + ".name");

        Log.info("GroupId of LmGroupMeterDisconnect created is : " + groupId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        JsonPath jsonPath = getResponse.jsonPath();
        context.setAttribute("MR_GrpName", jsonPath.get(meterDisconnectPaoTypeStr + ".name"));

        assertTrue("Name Should be : " + name, name.equals((String) jsonPath.get(meterDisconnectPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + meterDisconnectPaoTypeStr,
            meterDisconnectPaoTypeStr.equals(jsonPath.get(meterDisconnectPaoTypeStr + ".type")));
        assertTrue("kWCapacity Should be : " + kWCapacity,
            kWCapacity.equals(jsonPath.get(meterDisconnectPaoTypeStr + ".kWCapacity")));
        boolean disableGroup = (boolean) jsonPath.get(meterDisconnectPaoTypeStr + ".disableGroup");
        assertTrue("Group Should be disabled : ", !disableGroup);
        boolean disableControl = (boolean) jsonPath.get(meterDisconnectPaoTypeStr + ".disableControl");
        assertTrue("Control Should be disabled : ", !disableControl);
        Log.endTestCase("Test02_LmGroupMeterDisconnect_Get");
    }

    @Test(priority = 2)
    public void Test03_LmGroupMeterDisconnect_Update(ITestContext context) {
        Log.startTestCase("Test03_LmGroupMeterDisconnect_Update");

        String groupId = context.getAttribute("MeterDisconnectgroupId").toString();
        Float kWCapacity = (float) 888.766;
        String name = "Test Meter Disconnect_Update";
        context.setAttribute("MR_GrpName", name);

        JSONObject payload = JsonFileReader.updateLoadGroup(meterDisconnectpayloadfile, "id", groupId);
        payload = JsonFileReader.updateLoadGroup(payload, "kWCapacity", kWCapacity.toString());
        payload = JsonFileReader.updateLoadGroup(payload, "name", name);
        Log.info("Updated payload is :" + payload.toJSONString());

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", payload, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        JsonPath jsonPath = getupdatedResponse.jsonPath();
        assertTrue("Name Should be : " + name, name.equals(jsonPath.get(meterDisconnectPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + meterDisconnectPaoTypeStr,
            meterDisconnectPaoTypeStr.equals(jsonPath.get(meterDisconnectPaoTypeStr + ".type")));
        assertTrue("kWCapacity Should be : " + kWCapacity,
            kWCapacity.equals(jsonPath.get(meterDisconnectPaoTypeStr + ".kWCapacity")));
        Log.endTestCase("Test03_LmGroupMeterDisconnect_Update");

    }
}
