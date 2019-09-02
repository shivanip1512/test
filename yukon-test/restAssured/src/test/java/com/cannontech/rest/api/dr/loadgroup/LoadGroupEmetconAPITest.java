package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

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

public class LoadGroupEmetconAPITest {

    private final static String emetconPaoTypeStr = "LM_GROUP_EMETCON";
    private final static String emetconPayloadFile = "loadgroup\\lmGroupEmetconCreate.json";
    private final static String filePath = System.getProperty("user.dir") + "\\src\\test\\resources\\testDataFiles\\LoadGroupData.xlsx";
    private final static String sheetName = "Emetcon";

    /**
     * This test case validates creation of Emetcon load group with default values provided in payload json
     * file
     */
    @Test
    public void loadGroupEmetcon_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupEmetcon_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", emetconPayloadFile);
        String groupId = createResponse.path("groupId").toString();
        context.setAttribute("emetconGroupId", groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("loadGroupEmetcon_01_Create");

    }

    /**
     * This test case validates retrieval(Get) of Emetcon load group and validates response
     */
    @Test(dependsOnMethods = { "loadGroupEmetcon_01_Create" })
    public void loadGroupEmetcon_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupEmetcon_02_Get");
        String groupId = context.getAttribute("emetconGroupId").toString();
        JSONObject jo = JsonFileReader.readJsonFileAsJSONObject(emetconPayloadFile);
        JsonPath jp = new JsonPath(jo.toJSONString());
        int routeId = jp.getInt(emetconPaoTypeStr + ".routeId");
        String name = jp.get(emetconPaoTypeStr + ".name");

        Log.info("GroupId of LmGroupEmetcon created is : " + groupId);

        ExtractableResponse<?> response = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        JsonPath jsonPath = response.jsonPath();
        context.setAttribute("Emetcon_GrpName", jsonPath.get(emetconPaoTypeStr + ".name"));

        assertTrue("Name Should be : " + name, name.equals((String) jsonPath.get(emetconPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + emetconPaoTypeStr,
            emetconPaoTypeStr.equals(jsonPath.get(emetconPaoTypeStr + ".type")));
        assertTrue("routeId Should be : " + routeId, routeId == (jsonPath.getInt(emetconPaoTypeStr + ".routeId")));

        Log.endTestCase("loadGroupEmetcon_02_Get");

    }

    /**
     * This test case validates updation of Emetcon load group and validates response with updated values
     */
    @Test(dependsOnMethods = { "loadGroupEmetcon_01_Create" })
    public void loadGroupEmetcon_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupEmetcon_03_Update");

        String groupId = context.getAttribute("emetconGroupId").toString();
        String relayUsage = "RELAY_B";
        String name = "Auto_EmetconGroup_Update2";
        context.setAttribute("emetcon_UpdateGrpName", name);

        JSONObject payload = JsonFileReader.updateLoadGroup(emetconPayloadFile, "id", groupId);
        payload = JsonFileReader.updateLoadGroup(payload, "relayUsage", relayUsage);
        payload = JsonFileReader.updateLoadGroup(payload, "name", name);
        Log.info("Updated payload is :" + payload.toJSONString());

        ExtractableResponse<?> response = ApiCallHelper.post("updateloadgroup", payload, groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        JsonPath jsonPath = getupdatedResponse.jsonPath();
        assertTrue("Name Should be : " + name, name.equals(jsonPath.get(emetconPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + emetconPaoTypeStr,
            emetconPaoTypeStr.equals(jsonPath.get(emetconPaoTypeStr + ".type")));
        assertTrue("relayUsage Should be : " + relayUsage,
            relayUsage.equals(jsonPath.get(emetconPaoTypeStr + ".relayUsage")));
        Log.endTestCase("loadGroupEmetcon_04_Update");

    }

    @Test(enabled = false)
    public void loadGroupEmetcon_04_Copy(ITestContext context) {

    }

    /**
     * This test case validates deletion of Emetcon load group
     */
    @Test(dependsOnMethods = { "loadGroupEmetcon_01_Create" })
    public void loadGroupEmetcon_05_Delete(ITestContext context) {

        String expectedMessage = "Id not found";

        Log.startTestCase("loadGroupEmetcon_05_Delete");
        JSONObject payload = JsonFileReader.updateJsonFile("loadgroup\\delete.json", "name",
            context.getAttribute("emetcon_UpdateGrpName").toString());
        Log.info("Delete payload is : " + payload);
        ExtractableResponse<?> response =
            ApiCallHelper.delete("deleteloadgroup", payload, context.getAttribute("emetconGroupId").toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> response2 =
            ApiCallHelper.get("getloadgroup", context.getAttribute("emetconGroupId").toString());
        assertTrue("Status code should be 400", response2.statusCode() == 400);

        JsonPath jsonPath = response2.jsonPath();
        assertTrue("Expected error message Should be : " + expectedMessage,
            expectedMessage.equals(jsonPath.get("message")));

        Log.endTestCase("loadGroupEmetcon_05_Delete");

    }

    /**
     * This test case validates negative scenarios of Emetcon load group with different input data provided in
     * DataProviderClass
     */
    @Test(dataProvider = "EmetconAddressData")
    public void loadGroupEmetcon_06_AddressValidation(String goldAddress, String silverAddress, String expectedErrorMsg,
            String expectedStatusCode) {

        Log.startTestCase("loadGroupEmetcon_06_AddressValidation");

        JSONObject payload = JsonFileReader.updateLoadGroup(emetconPayloadFile, "goldAddress", goldAddress);
        payload = JsonFileReader.updateLoadGroup(payload, "silverAddress", silverAddress);
        payload = JsonFileReader.updateLoadGroup(payload, "addressUsage", "GOLD");

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", payload);
        Integer statusCode = response.statusCode();
        assertTrue("Status code should be " + expectedStatusCode, expectedStatusCode.equals(statusCode.toString()));

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct",
            expectedErrorMsg.equals(jsonPath.get("fieldErrors.code[0]")));

        Log.endTestCase("loadGroupEmetcon_06_AddressValidation");
    }
    
    /**
     * DataProvider provides data to test method in the form of object array
     * Data provided in test data sheet -
     * col1 : goldAddress
     * col2 : silverAddress
     * col3 : expectedErrorMsg
     * col4 : expectedStatusCode
     */
    @DataProvider(name = "EmetconAddressData")
    public Object[][] getEmetconAddressData() throws Exception {

        Object[][] testObjArray = ExcelUtils.getTableArray(filePath, sheetName);
        return testObjArray;
    }

}
