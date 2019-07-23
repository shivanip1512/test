package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

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

	@Test
	public void Test01_LmGroupItron_Create(ITestContext context) {
    	Log.startTestCase("Test_LmGroupItron_Create");
    	ExtractableResponse<?> createResponse =
                ApiCallHelper.post("saveloadgroup", "loadgroup\\lmGroupItronCreate.json");
    	String groupId = createResponse.path("groupId").toString();
    	context.setAttribute("itrongroupId", groupId);
    	assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    	assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("Test_LmGroupItron_Create");
    }
	 
	@Test
	public void Test02_LmGroupItron_Get(ITestContext context) {
		Log.startTestCase("Test_LmGroupItron_Get");    
		String groupId = context.getAttribute("itrongroupId").toString();
		
		Log.info("GroupId of LmGroupItron created is : "+groupId);
		
		System.out.println(context.getName());
	    ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
	    assertTrue("Status code should be 200", getResponse.statusCode() == 200);
	
	    JsonPath jsonPath = getResponse.jsonPath();
	    HashMap loadGroupData = jsonPath.get("LM_GROUP_ITRON");
	    String name = (String) loadGroupData.get("name");
	    assertTrue("Name Should be : LM_Group_Itron_Name", "LM_Group_Itron_Name".equals(name));
	    String type = (String) loadGroupData.get("type");
	    assertTrue("Type Should be : LM_GROUP_ITRON", "LM_GROUP_ITRON".equals(type));
	    float kWCapacity = (float) loadGroupData.get("kWCapacity");
	    assertTrue("kWCapacity Should be : 23", 23 == kWCapacity);
	    boolean disableGroup = (boolean) loadGroupData.get("disableGroup");
	    assertTrue("Group Should be disabled : ", !disableGroup);
	    boolean disableControl = (boolean) loadGroupData.get("disableControl");
	    assertTrue("Control Should be disabled : ", !disableControl);
	    int virtualRelayId = (int) loadGroupData.get("virtualRelayId");
	    assertTrue("VirtualRelayId Should be : 5", 5 == virtualRelayId);
	    Log.endTestCase("Test_LmGroupItron_Get");	
	}
	
	@Test
	public void Test03_LmGroupItron_Update(ITestContext context) {
		Log.startTestCase("Test_LmGroupItron_Update");
		String groupId = context.getAttribute("itrongroupId").toString();
		
		JSONObject payload = JsonFileReader.updateLoadGroup("loadgroup\\lmGroupItronCreate.json", "id", groupId);
		payload = JsonFileReader.updateLoadGroup(payload, "kWCapacity", "30");
		payload = JsonFileReader.updateLoadGroup(payload, "name", "LM_Group_Itron_Name_Update");
		Log.info("Updated payload is :" +payload.toJSONString());
		
		ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup",payload, groupId);
	    assertTrue("Status code should be 200", getResponse.statusCode() == 200);
	    
	    ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);
	    
	    JsonPath jsonPath = getupdatedResponse.jsonPath();
	    HashMap loadGroupData = jsonPath.get("LM_GROUP_ITRON");
	    String name = (String) loadGroupData.get("name");
	    assertTrue("Name Should be :LM_Group_Itron_Name_Update", "LM_Group_Itron_Name_Update".equals(name));
	    String type = (String) loadGroupData.get("type");
	    assertTrue("Type Should be : LM_GROUP_ITRON", "LM_GROUP_ITRON".equals(type));
	    float kWCapacity = (float) loadGroupData.get("kWCapacity");
	    assertTrue("kWCapacity Should be : 30", 30 == kWCapacity);
	    Log.endTestCase("Test_LmGroupItron_Update");		
	}
	
	/*	@Test
	public void Test04_LmGroupNest_Copy(ITestContext context) {
		
	}
	
	@Test
	public void Test05_LmGroupNest_Delete() {
		
	}
	*/
	
	@Test(dataProvider = "GroupNameData", dataProviderClass = DataProviderClass.class)
	public void Test06_ItronGroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {
		
		Log.startTestCase("Test06_ItronGroupNameValidation");
		
		JSONObject payload = JsonFileReader.updateLoadGroup(payloadfile, "name", groupName);
		ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup",payload);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
		
		Log.endTestCase("Test06_ItronGroupNameValidation");
	}
	
	@Test(dataProvider = "KwCapacityData", dataProviderClass = DataProviderClass.class)
	public void Test07_ItronKwCapacityValidation(Float kwCapacity, String expectedFieldCode, int expectedStatusCode) {
		
		Log.startTestCase("Test07_NestKwCapacityValidation");
		
		JSONObject payload = JsonFileReader.updateLoadGroup(payloadfile, "kWCapacity", kwCapacity.toString());
		ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup",payload);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
		
		Log.endTestCase("Test07_ItronKwCapacityValidation");
	}
	
	@Test(dataProvider = "VirtualRelayIdData", dataProviderClass = DataProviderClass.class)
	public void Test07_ItronVirtualRelayIdValidation(Float virtualRelayId, String expectedFieldCode, int expectedStatusCode) {
		
		Log.startTestCase("Test07_ItronVirtualRelayIdValidation");
		
		JSONObject payload = JsonFileReader.updateLoadGroup(payloadfile, "virtualRelayId", virtualRelayId.toString());
		ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", payload);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
		
		Log.endTestCase("Test07_ItronVirtualRelayIdValidation");
	}
}
