package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.utilities.ExcelUtils;
import com.cannontech.rest.api.utilities.JsonFileReader;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupRFNExpresscom {

    private final static String expcomPaoNameStr = "LM_GROUP_EXPRESSCOMM";
    private final static String expcomPaoTypeStr = "LM_GROUP_RFN_EXPRESSCOMM";
    private final static String expcomPayloadFile = "loadgroup\\lmGroupRFNExpresscomCreate.json";

    /**
     * This test case validates creation of RFNExpresscomm load group with default values provided in payload
     * json
     * file
     */
    @Test
    public void loadGroupRFNExpresscom_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupRFNExpresscom_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", expcomPayloadFile);
        String groupId = createResponse.path("groupId").toString();
        context.setAttribute("RFNexpresscomGroupId", groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("loadGroupRFNExpresscom_01_Create");

    }

    /**
     * This test case validates retrieval(Get) of RFNExpresscomm load group and validates response
     */
    @Test(dependsOnMethods = { "loadGroupRFNExpresscom_01_Create" })
    public void loadGroupRFNExpresscom_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupRFNExpresscom_02_Get");
        String groupId = context.getAttribute("RFNexpresscomGroupId").toString();
        JSONObject jo = JsonFileReader.readJsonFileAsJSONObject(expcomPayloadFile);
        JsonPath jp = new JsonPath(jo.toJSONString());
        int routeId = jp.getInt(expcomPaoNameStr + ".routeId");
        String name = jp.get(expcomPaoNameStr + ".name");

        Log.info("GroupId of LmGroupRFNExpresscomm created is : " + groupId);

        ExtractableResponse<?> response = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        JsonPath jsonPath = response.jsonPath();
        context.setAttribute("RFNExpresscomm_GrpName", jsonPath.get(expcomPaoNameStr + ".name"));

        assertTrue("Name Should be : " + name, name.equals((String) jsonPath.get(expcomPaoNameStr + ".name")));
        assertTrue("Type Should be : " + expcomPaoTypeStr,
            expcomPaoTypeStr.equals(jsonPath.get(expcomPaoNameStr + ".type")));
        assertTrue("routeId Should be : " + routeId, routeId == (jsonPath.getInt(expcomPaoNameStr + ".routeId")));

        Log.endTestCase("loadGroupRFNExpresscom_02_Get");
    }

    /**
     * This test case validates updation of RFNExpresscomm load group and validates response with updated
     * values
     */
    @Test(dependsOnMethods = { "loadGroupRFNExpresscom_01_Create" })
    public void loadGroupRFNExpresscom_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupRFNExpresscom_03_Update");

        String groupId = context.getAttribute("RFNexpresscomGroupId").toString();
        Float kWCapacity = (float) 785;
        String name = "Auto_RFNExpresscommGroup_Update";
        context.setAttribute("RFNexpresscom_UpdateGrpName", name);

        JSONObject payload = JsonFileReader.updateLoadGroup(expcomPayloadFile, "id", groupId);
        payload = JsonFileReader.updateLoadGroup(payload, "kWCapacity", kWCapacity.toString());
        payload = JsonFileReader.updateLoadGroup(payload, "name", name);
        Log.info("Updated payload is :" + payload.toJSONString());

        ExtractableResponse<?> response = ApiCallHelper.post("updateloadgroup", payload, groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        JsonPath jsonPath = getupdatedResponse.jsonPath();
        assertTrue("Name Should be : " + name, name.equals(jsonPath.get(expcomPaoNameStr + ".name")));
        assertTrue("Type Should be : " + expcomPaoTypeStr,
            expcomPaoTypeStr.equals(jsonPath.get(expcomPaoNameStr + ".type")));
        assertTrue("kWCapacity Should be : " + kWCapacity,
            kWCapacity.equals(jsonPath.get(expcomPaoNameStr + ".kWCapacity")));
        Log.endTestCase("loadGroupRFNExpresscom_04_Update");
    }

    @Test(enabled = false)
    public void loadGroupRFNExpresscom_04_Copy(ITestContext context) {

    }

    /**
     * This test case validates deletion of RFNExpresscomm load group
     */
    @Test(dependsOnMethods = { "loadGroupRFNExpresscom_01_Create" })
    public void loadGroupRFNExpresscom_05_Delete(ITestContext context) {

        String expectedMessage = "Id not found";

        Log.startTestCase("loadGroupRFNExpresscom_05_Delete");
        JSONObject payload = JsonFileReader.updateJsonFile("loadgroup\\delete.json", "name",
            context.getAttribute("RFNexpresscom_UpdateGrpName").toString());
        Log.info("Delete payload is : " + payload);
        ExtractableResponse<?> response =
            ApiCallHelper.delete("deleteloadgroup", payload, context.getAttribute("RFNexpresscomGroupId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> response2 =
            ApiCallHelper.get("getloadgroup", context.getAttribute("RFNexpresscomGroupId").toString());
        assertTrue("Status code should be 400", response2.statusCode() == 400);

        JsonPath jsonPath = response2.jsonPath();
        assertTrue("Expected error message Should be : " + expectedMessage,
            expectedMessage.equals(jsonPath.get("message")));

        Log.endTestCase("loadGroupRFNExpresscom_05_Delete");
    }
}
