package com.cannontech.rest.api.dr.setup;

import static io.restassured.RestAssured.baseURI;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiRequestHelper;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupSetupApiControllerTest {

    @BeforeTest
    public void setup() {
        baseURI = ApiRequestHelper.getProperty("baseURI");

    }

    @Test
    public void Test_LmGroupMeterDisconnect_Create() {
        ExtractableResponse<?> response = ApiRequestHelper.post("saveloadgroup", "loadgroup\\lmGroupMeterDisconnectCreate.json");
        String paoId = ("paoId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", response.statusCode() == 200);
    }

    @Test
    public void Test_LmGroupMeterDisconnect_Get() {

        ExtractableResponse<?> createResponse =
            ApiRequestHelper.post("saveloadgroup", "loadgroup\\lmGroupMeterDisconnectGet.json");
        String paoId = createResponse.path("paoId").toString();
        assertTrue("PAO ID should not be Null", paoId != null);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);

        ExtractableResponse<?> getResponse = ApiRequestHelper.get("getloadgroup", paoId);
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
