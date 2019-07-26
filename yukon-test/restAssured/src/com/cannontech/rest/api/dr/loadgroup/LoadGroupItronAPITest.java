package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.data.DataProviderClass;
import com.cannontech.rest.api.utilities.JsonFileReader;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupItronAPITest {
	String LGBase = "LM_GROUP_ITRON";
	String type = "LM_GROUP_ITRON";
	String payloadfile = "loadgroup\\lmGroupItronCreate.json";
	private final static String itronPaoTypeStr = "LM_GROUP_ITRON";
    private final static String itronPayloadFile = "loadgroup\\lmGroupItronCreate.json";

	@Test
	public void loadGroupItron_01_Create(ITestContext context) {
    	Log.startTestCase("loadGroupItron_01_Create");
    	ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", itronPayloadFile);
    	String groupId = createResponse.path("groupId").toString();
    	context.setAttribute("itrongroupId", groupId);
    	assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    	assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("loadGroupItron_01_Create");
    }
	 
	@Test
	public void loadGroupItron_02_Get(ITestContext context) {
	    Log.startTestCase("loadGroupItron_02_Get");    
	    String groupId = context.getAttribute("itrongroupId").toString();
        JSONObject jo = JsonFileReader.readJsonFileAsJSONObject(itronPayloadFile);
        JsonPath jp = new JsonPath(jo.toJSONString());
        Float kWCapacity = jp.getFloat(itronPaoTypeStr+".kWCapacity"); 
        String name = jp.get(itronPaoTypeStr+".name"); 
        Integer virtualRelayId = jp.getInt(itronPaoTypeStr+".virtualRelayId");
        
        Log.info("GroupId of LmGroupItron created is : "+groupId);
        
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
    
        JsonPath jsonPath = getResponse.jsonPath();
        context.setAttribute("Itron_GrpName", jsonPath.get(itronPaoTypeStr+".name"));

        assertTrue("Name Should be : "+name, name.equals(jsonPath.get(itronPaoTypeStr+".name")));
        assertTrue("Type Should be : "+type, type.equals(jsonPath.get(itronPaoTypeStr+".type")));
        assertTrue("kWCapacity Should be : "+kWCapacity, kWCapacity.equals(jsonPath.get(itronPaoTypeStr+".kWCapacity")));
        boolean disableGroup = (boolean) jsonPath.get(LGBase+".disableGroup");
        assertTrue("Group Should be disabled : ", !disableGroup);
        boolean disableControl = (boolean) jsonPath.get(LGBase+".disableControl");
        assertTrue("Control Should be disabled : ", !disableControl);
        assertTrue("VirtualRelayId Should be : "+virtualRelayId, virtualRelayId.equals(jsonPath.get(itronPaoTypeStr+".virtualRelayId")));
        Log.endTestCase("loadGroupItron_02_Get");   
	}
	
	@Test
	public void loadGroupItron_03_Update(ITestContext context) {
		Log.startTestCase("loadGroupItron_03_Update");
		String groupId = context.getAttribute("itrongroupId").toString();
        Float kWCapacity = (float) 888;
        Integer virtualRelayId = (int) 7;
        String name = "LM_Group_Itron_Name_Update";
        context.setAttribute("Itron_GrpName", name);
        
        JSONObject payload = JsonFileReader.updateLoadGroup(itronPayloadFile, "id", groupId);
        payload = JsonFileReader.updateLoadGroup(payload, "kWCapacity", kWCapacity.toString());
        payload = JsonFileReader.updateLoadGroup(payload, "virtualRelayId", virtualRelayId.toString());
        payload = JsonFileReader.updateLoadGroup(payload, "name", name);
        Log.info("Updated payload is :" +payload.toJSONString());
        
        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup",payload, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        
        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);
        
        JsonPath jsonPath = getupdatedResponse.jsonPath();
        assertTrue("Name Should be : "+name, name.equals(jsonPath.get(itronPaoTypeStr+".name")));
        assertTrue("Type Should be : "+type, type.equals(jsonPath.get(itronPaoTypeStr+".type")));
        assertTrue("kWCapacity Should be : "+kWCapacity, kWCapacity.equals(jsonPath.get(itronPaoTypeStr+".kWCapacity")));
        assertTrue("Virtual Relay Id Should be : "+virtualRelayId, virtualRelayId.equals(jsonPath.get(itronPaoTypeStr+".virtualRelayId")));
	    Log.endTestCase("loadGroupItron_03_Update");		
	}
	
	/*	@Test
	public void Test04_LmGroupNest_Copy(ITestContext context) {
		
	}
	
	@Test
	public void Test05_LmGroupNest_Delete() {
		
	}
	*/
	
	@Test(dataProvider = "GroupNameData", dataProviderClass = DataProviderClass.class)
	public void loadGroupItron_06_GroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {
		
		Log.startTestCase("loadGroupItron_06_GroupNameValidation");
		
		JSONObject payload = JsonFileReader.updateLoadGroup(itronPayloadFile, "name", groupName);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup",payload);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
		
		Log.endTestCase("loadGroupItron_06_GroupNameValidation");
	}
	
	@Test(dataProvider = "KwCapacityData", dataProviderClass = DataProviderClass.class)
	public void loadGroupItron_07_KwCapacityValidation(Float kwCapacity, String expectedFieldCode, int expectedStatusCode) {
		
		Log.startTestCase("loadGroupItron_07_KwCapacityValidation");
		
		JSONObject payload = JsonFileReader.updateLoadGroup(itronPayloadFile, "kWCapacity", kwCapacity.toString());
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup",payload);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
        
		Log.endTestCase("loadGroupItron_0y_KwCapacityValidation");
	}
	
	@Test(dataProvider = "VirtualRelayIdData", dataProviderClass = DataProviderClass.class)
	public void loadGroupItron_08_VirtualRelayIdValidation(Integer virtualRelayId, String expectedFieldCode, int expectedStatusCode) {
		
		Log.startTestCase("loadGroupItron_08_VirtualRelayIdValidation");
		
		JSONObject payload = JsonFileReader.updateLoadGroup(itronPayloadFile, "virtualRelayId", virtualRelayId.toString());
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", payload);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
		
		Log.endTestCase("loadGroupItron_08_VirtualRelayIdValidation");
	}
}
