package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.data.DataProviderClass;
import com.cannontech.rest.api.utilities.JsonFileReader;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupNestAPITest {

    private final static String nestPaoTypeStr = "LM_GROUP_NEST";
    private final static String nestPayloadFile = "loadgroup\\lmGroupNestCreate.json";

    @Test
    public void loadGroupNest_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupNest_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", nestPayloadFile);
        String groupId = createResponse.path("groupId").toString();
        context.setAttribute("nestgroupId", groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("loadGroupNest_01_Create");
    }

    @Test
    public void loadGroupNest_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupNest_02_Get");
        String groupId = context.getAttribute("nestgroupId").toString();
        JSONObject jo = JsonFileReader.readJsonFileAsJSONObject(nestPayloadFile);
        JsonPath jp = new JsonPath(jo.toJSONString());
        Float kWCapacity = jp.getFloat(nestPaoTypeStr + ".kWCapacity");
        String name = jp.get(nestPaoTypeStr + ".name");

        Log.info("GroupId of LmGroupNest created is : " + groupId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        JsonPath jsonPath = getResponse.jsonPath();
        context.setAttribute("Nest_GrpName", jsonPath.get(nestPaoTypeStr + ".name"));

        assertTrue("Name Should be : " + name, name.equals((String) jsonPath.get(nestPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + nestPaoTypeStr,
            nestPaoTypeStr.equals(jsonPath.get(nestPaoTypeStr + ".type")));
        assertTrue("kWCapacity Should be : " + kWCapacity,
            kWCapacity.equals(jsonPath.get(nestPaoTypeStr + ".kWCapacity")));
        boolean disableGroup = (boolean) jsonPath.get(nestPaoTypeStr + ".disableGroup");
        assertTrue("Group Should be disabled : ", !disableGroup);
        boolean disableControl = (boolean) jsonPath.get(nestPaoTypeStr + ".disableControl");
        assertTrue("Control Should be disabled : ", !disableControl);
        Log.endTestCase("loadGroupNest_02_Get");
    }

    @Test
    public void loadGroupNest_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupNest_03_Update");

        String groupId = context.getAttribute("nestgroupId").toString();
        Float kWCapacity = (float) 888;
        String name = "Auto_LM_Group_Nest_Update";
        context.setAttribute("Nest_GrpName", name);

        JSONObject payload = JsonFileReader.updateLoadGroup(nestPayloadFile, "id", groupId);
        payload = JsonFileReader.updateLoadGroup(payload, "kWCapacity", kWCapacity.toString());
        payload = JsonFileReader.updateLoadGroup(payload, "name", name);
        Log.info("Updated payload is :" + payload.toJSONString());

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", payload, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        JsonPath jsonPath = getupdatedResponse.jsonPath();
        assertTrue("Name Should be : " + name, name.equals(jsonPath.get(nestPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + nestPaoTypeStr,
            nestPaoTypeStr.equals(jsonPath.get(nestPaoTypeStr + ".type")));
        assertTrue("kWCapacity Should be : " + kWCapacity,
            kWCapacity.equals(jsonPath.get(nestPaoTypeStr + ".kWCapacity")));
        Log.endTestCase("loadGroupNest_03_Update");
    }

    @Test(enabled = false)
    public void loadGroupNest_04_Copy(ITestContext context) {

    }

    @Test(enabled = false)
    public void loadGroupNest_05_Delete() {

    }

    @Test(dataProvider = "GroupNameData", dataProviderClass = DataProviderClass.class)
    public void loadGroupNest_06_GroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadGroupNest_06_GroupNameValidation");

        JSONObject payload = JsonFileReader.updateLoadGroup(nestPayloadFile, "name", groupName);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", payload);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct",
            expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));

        Log.endTestCase("loadGroupNest_06_GroupNameValidation");

    }

    @Test(dataProvider = "KwCapacityData", dataProviderClass = DataProviderClass.class)
    public void loadGroupNest_07_KwCapacityValidation(Float kwCapacity, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadGroupNest_07_KwCapacityValidation");

        JSONObject payload = JsonFileReader.updateLoadGroup(nestPayloadFile, "kWCapacity", kwCapacity.toString());
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", payload);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct",
            expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));

        Log.endTestCase("loadGroupNest_07_KwCapacityValidation");
    }
}
