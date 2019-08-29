package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.data.DataProviderClass;
import com.cannontech.rest.api.utilities.ExcelUtils;
import com.cannontech.rest.api.utilities.JsonFileReader;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupExpressComAPITest {

    private final static String expcomPaoTypeStr = "LM_GROUP_EXPRESSCOMM";
    private final static String expcomPayloadFile = "loadgroup\\lmGroupExpresscomCreate.json";
    public static String filePath = System.getProperty("user.dir") + "\\resources\\testDataFiles\\LoadGroupData.xlsx";
    public static String sheetName = "Expresscom";

    /**
     * This test case validates creation of Expresscomm load group with default values provided in payload
     * json
     * file
     */
    @Test
    public void loadGroupExpresscom_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", expcomPayloadFile);
        String groupId = createResponse.path("groupId").toString();
        context.setAttribute("expresscomGroupId", groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("loadGroupExpresscom_01_Create");

    }

    /**
     * This test case validates retrieval(Get) of Expresscomm load group and validates response
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_01_Create" })
    public void loadGroupExpresscom_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_02_Get");
        String groupId = context.getAttribute("expresscomGroupId").toString();
        JSONObject jo = JsonFileReader.readJsonFileAsJSONObject(expcomPayloadFile);
        JsonPath jp = new JsonPath(jo.toJSONString());
        int routeId = jp.getInt(expcomPaoTypeStr + ".routeId");
        String name = jp.get(expcomPaoTypeStr + ".name");

        Log.info("GroupId of LmGroupExpresscomm created is : " + groupId);

        ExtractableResponse<?> response = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        JsonPath jsonPath = response.jsonPath();
        context.setAttribute("Expresscomm_GrpName", jsonPath.get(expcomPaoTypeStr + ".name"));

        assertTrue("Name Should be : " + name, name.equals((String) jsonPath.get(expcomPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + expcomPaoTypeStr,
            expcomPaoTypeStr.equals(jsonPath.get(expcomPaoTypeStr + ".type")));
        assertTrue("routeId Should be : " + routeId, routeId == (jsonPath.getInt(expcomPaoTypeStr + ".routeId")));

        Log.endTestCase("loadGroupExpresscom_02_Get");

    }

    /**
     * This test case validates updation of Expresscomm load group and validates response with updated values
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_01_Create" })
    public void loadGroupExpresscom_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupExpresscom_03_Update");

        String groupId = context.getAttribute("expresscomGroupId").toString();
        Float kWCapacity = (float) 785;
        String name = "Auto_ExpresscommGroup_Update";
        context.setAttribute("expresscom_UpdateGrpName", name);

        JSONObject payload = JsonFileReader.updateLoadGroup(expcomPayloadFile, "id", groupId);
        payload = JsonFileReader.updateLoadGroup(payload, "kWCapacity", kWCapacity.toString());
        payload = JsonFileReader.updateLoadGroup(payload, "name", name);
        Log.info("Updated payload is :" + payload.toJSONString());

        ExtractableResponse<?> response = ApiCallHelper.post("updateloadgroup", payload, groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        JsonPath jsonPath = getupdatedResponse.jsonPath();
        assertTrue("Name Should be : " + name, name.equals(jsonPath.get(expcomPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + expcomPaoTypeStr,
            expcomPaoTypeStr.equals(jsonPath.get(expcomPaoTypeStr + ".type")));
        assertTrue("kWCapacity Should be : " + kWCapacity,
            kWCapacity.equals(jsonPath.get(expcomPaoTypeStr + ".kWCapacity")));
        Log.endTestCase("loadGroupExpresscom_04_Update");

    }

    @Test(enabled = false)
    public void loadGroupExpresscom_04_Copy(ITestContext context) {

    }

    /**
     * This test case validates deletion of Expresscomm load group
     */
    @Test(dependsOnMethods = { "loadGroupExpresscom_01_Create" })
    public void loadGroupExpresscom_05_Delete(ITestContext context) {

        String expectedMessage = "Id not found";

        Log.startTestCase("loadGroupExpresscom_05_Delete");
        JSONObject payload = JsonFileReader.updateJsonFile("loadgroup\\delete.json", "name",
            context.getAttribute("expresscom_UpdateGrpName").toString());
        Log.info("Delete payload is : " + payload);
        ExtractableResponse<?> response =
            ApiCallHelper.delete("deleteloadgroup", payload, context.getAttribute("expresscomGroupId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> response2 =
            ApiCallHelper.get("getloadgroup", context.getAttribute("expresscomGroupId").toString());
        assertTrue("Status code should be 400", response2.statusCode() == 400);

        JsonPath jsonPath = response2.jsonPath();
        assertTrue("Expected error message Should be : " + expectedMessage,
            expectedMessage.equals(jsonPath.get("message")));

        Log.endTestCase("loadGroupExpresscom_05_Delete");

    }

    /**
     * This test case validates negative scenarios of Emetcon load group with different input data provided in
     * DataProviderClass
     */
    @Test(dataProvider = "ExpresscomAddressData")
    public void loadGroupExpresscom_06_PhysicalAddressValidation(String spid, String geoId, String subId, String zip,
            String user, String expectedErrorMsg, String expectedStatusCode) {

        Log.startTestCase("loadGroupExpresscom_06_PhysicalAddressValidation");
        String payloadFile = "loadgroup\\lmGroupExpresscomAll.json";

        // update payload with test data provided
        JSONObject payload = JsonFileReader.updateLoadGroup(payloadFile, "serviceProvider", spid);
        payload = JsonFileReader.updateLoadGroup(payload, "geo", geoId);
        payload = JsonFileReader.updateLoadGroup(payload, "substation", subId);
        payload = JsonFileReader.updateLoadGroup(payload, "zip", zip);
        payload = JsonFileReader.updateLoadGroup(payload, "user", user);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", payload);
        Integer statusCode = response.statusCode();
        assertTrue("Status code should be " + expectedStatusCode, expectedStatusCode.equals(statusCode.toString()));
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct",
            expectedErrorMsg.equals(jsonPath.get("fieldErrors.code[0]")));

        Log.endTestCase("loadGroupExpresscom_06_PhysicalAddressValidation");
    }

    /**
     * DataProvider provides data to test method in the form of object array
     * Data provided in test data sheet -
     * col1 : serviceProviderId
     * col2 : geoId
     * col3 : substationId
     * col4 : feeder
     * col5 : zip
     * col6 : user
     * col7 : Expected field errors code in response
     * col8 : Expected response code
     */
    @DataProvider(name = "ExpresscomAddressData")
    public Object[][] getExpresscomAddressData() throws Exception {

        Object[][] testObjArray = ExcelUtils.getTableArray(filePath, sheetName);
        return testObjArray;
    }
}
