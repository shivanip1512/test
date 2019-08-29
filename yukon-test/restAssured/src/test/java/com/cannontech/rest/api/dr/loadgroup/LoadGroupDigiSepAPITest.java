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

public class LoadGroupDigiSepAPITest {
    private final static String digiSepPaoTypeStr = "LM_GROUP_NEST";
    private final static String digiSepPayloadFile = "loadgroup\\lmGroupDigiSepCreate.json";

    @Test
    public void loadGroupDigiSep_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupDigiSep_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", digiSepPayloadFile);
        String groupId = createResponse.path("groupId").toString();
        context.setAttribute("digiSepgroupId", groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("loadGroupDigiSep_01_Create");
    }

    @Test
    public void loadGroupDigiSep_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupNest_02_Get");
        String groupId = context.getAttribute("digiSepgroupId").toString();
        JSONObject jo = JsonFileReader.readJsonFileAsJSONObject(digiSepPayloadFile);
        JsonPath jp = new JsonPath(jo.toJSONString());
        Float kWCapacity = jp.getFloat(digiSepPaoTypeStr + ".kWCapacity");
        String name = jp.get(digiSepPaoTypeStr + ".name");

        Log.info("GroupId of LmGroupDigiSep created is : " + groupId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        JsonPath jsonPath = getResponse.jsonPath();
        context.setAttribute("DigiSep_GrpName", jsonPath.get(digiSepPaoTypeStr + ".name"));

        assertTrue("Name Should be : " + name, name.equals((String) jsonPath.get(digiSepPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + digiSepPaoTypeStr,
            digiSepPaoTypeStr.equals(jsonPath.get(digiSepPaoTypeStr + ".type")));
        assertTrue("kWCapacity Should be : " + kWCapacity,
            kWCapacity.equals(jsonPath.get(digiSepPaoTypeStr + ".kWCapacity")));

        boolean disableGroup = (boolean) jsonPath.get(digiSepPaoTypeStr + ".disableGroup");
        assertTrue("Group Should be disabled : ", !disableGroup);
        boolean disableControl = (boolean) jsonPath.get(digiSepPaoTypeStr + ".disableControl");
        assertTrue("Control Should be disabled : ", !disableControl);
        Log.endTestCase("loadGroupNest_02_Get");
    }

    @Test
    public void loadGroupDigiSep_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupDigiSep_03_Update");

        String groupId = context.getAttribute("digiSepgroupId").toString();
        Float kWCapacity = (float) 543.908;
        String name = "DigiSep-update";
        context.setAttribute("DigiSep_GrpName", name);

        JSONObject payload = JsonFileReader.updateLoadGroup(digiSepPayloadFile, "id", groupId);
        payload = JsonFileReader.updateLoadGroup(payload, "kWCapacity", kWCapacity.toString());
        payload = JsonFileReader.updateLoadGroup(payload, "name", name);
        Log.info("Updated payload is :" + payload.toJSONString());

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", payload, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        JsonPath jsonPath = getupdatedResponse.jsonPath();
        assertTrue("Name Should be : " + name, name.equals(jsonPath.get(digiSepPaoTypeStr + ".name")));
        assertTrue("Type Should be : " + digiSepPaoTypeStr,
            digiSepPaoTypeStr.equals(jsonPath.get(digiSepPaoTypeStr + ".type")));
        assertTrue("kWCapacity Should be : " + kWCapacity,
            kWCapacity.equals(jsonPath.get(digiSepPaoTypeStr + ".kWCapacity")));

        Log.endTestCase("loadGroupDigiSep_03_Update");
    }
}
