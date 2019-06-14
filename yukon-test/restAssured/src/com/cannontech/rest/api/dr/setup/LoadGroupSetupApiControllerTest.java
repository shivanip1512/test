package com.cannontech.rest.api.dr.setup;

import static io.restassured.RestAssured.baseURI;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupSetupApiControllerTest {

    @BeforeTest
    public void setup() {
        baseURI = ApiCallHelper.getProperty("baseURI");

    }

    @Test
    public void Test_LmGroupMeterDisconnect_Create() {
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", "loadgroup\\lmGroupMeterDisconnectCreate.json");
        String paoId = ("paoId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test
    public void Test_LmGroupMeterDisconnect_Get() {

        ExtractableResponse<?> createResponse =
            ApiCallHelper.post("saveloadgroup", "loadgroup\\lmGroupMeterDisconnectGet.json");
        String paoId = createResponse.path("paoId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", paoId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        JsonPath jsonPath = getResponse.jsonPath();
        HashMap loadGroupData = jsonPath.get("LoadGroupBase");
        String name = (String) loadGroupData.get("name");
        assertTrue("Name Should be : Meter_disconnect_get", "Meter_disconnect_get".equals(name));
        String type = (String) loadGroupData.get("type");
        assertTrue("Type Should be : LM_GROUP_METER_DISCONNECT", "LM_GROUP_METER_DISCONNECT".equals(type));
        float kWCapacity = (float) loadGroupData.get("kWCapacity");
        assertTrue("kWCapacity Should be : 123", 123 == kWCapacity);
        boolean disableGroup = (boolean) loadGroupData.get("disableGroup");
        assertTrue("Group Should be disabled : ", !disableGroup);
        boolean disableControl = (boolean) loadGroupData.get("disableControl");
        assertTrue("Control Should be disabled : ", !disableControl);
    }
}
