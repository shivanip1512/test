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
	
	
	String LGBase = "LM_GROUP_NEST";
	String type = "LM_GROUP_NEST";
	String payloadfile = "loadgroup\\lmGroupNestCreate.json";
	
	@Test
	public void Test01_LmGroupNest_Create(ITestContext context) {
		
		Log.startTestCase("Test01_LmGroupNest_Create");
    	ExtractableResponse<?> createResponse =
                ApiCallHelper.post("saveloadgroup", payloadfile);
    	String groupId = createResponse.path("groupId").toString();
    	context.setAttribute("nestgroupId", groupId);
    	assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    	assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("Test01_LmGroupNest_Create");
	}
	
	@Test
	public void Test02_LmGroupNest_Get(ITestContext context) {
		
		Log.startTestCase("Test02_LmGroupNest_Get");    
    	String groupId = context.getAttribute("nestgroupId").toString();
    	JSONObject jo = JsonFileReader.readJsonFileAsJSONObject(payloadfile);
		JsonPath jp = new JsonPath(jo.toJSONString());
    	Float kWCapacity = jp.getFloat(LGBase+".kWCapacity"); 
    	String name = jp.get(LGBase+".name");  	
    	
    	Log.info("GroupId of LmGroupNest created is : "+groupId);
    	
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        JsonPath jsonPath = getResponse.jsonPath();
        context.setAttribute("Nest_GrpName", jsonPath.get(LGBase+".name"));

        assertTrue("Name Should be : "+name, name.equals((String) jsonPath.get(LGBase+".name")));
        assertTrue("Type Should be : "+type, type.equals(jsonPath.get(LGBase+".type")));
        assertTrue("kWCapacity Should be : "+kWCapacity, kWCapacity.equals(jsonPath.get(LGBase+".kWCapacity")));
        boolean disableGroup = (boolean) jsonPath.get(LGBase+".disableGroup");
        assertTrue("Group Should be disabled : ", !disableGroup);
        boolean disableControl = (boolean) jsonPath.get(LGBase+".disableControl");
        assertTrue("Control Should be disabled : ", !disableControl);
        Log.endTestCase("Test02_LmGroupNest_Get");
	}
	
	@Test
	public void Test03_LmGroupNest_Update(ITestContext context) {
		
		Log.startTestCase("Test03_LmGroupNest_Update");    
    	
    	String groupId = context.getAttribute("nestgroupId").toString();
    	Float kWCapacity = (float) 888;
    	String name = "Auto_LM_Group_Nest_Update";
    	context.setAttribute("Nest_GrpName", name);
    	
    	JSONObject payload = JsonFileReader.updateLoadGroup(payloadfile, "id", groupId);
    	payload = JsonFileReader.updateLoadGroup(payload, "kWCapacity", kWCapacity.toString());
    	payload = JsonFileReader.updateLoadGroup(payload, "name", name);
    	Log.info("Updated payload is :" +payload.toJSONString());
    	
    	ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup",payload, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        
        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);
        
        JsonPath jsonPath = getupdatedResponse.jsonPath();
        assertTrue("Name Should be : "+name, name.equals(jsonPath.get(LGBase+".name")));
        assertTrue("Type Should be : "+type, type.equals(jsonPath.get(LGBase+".type")));
        assertTrue("kWCapacity Should be : "+kWCapacity, kWCapacity.equals(jsonPath.get(LGBase+".kWCapacity")));
        Log.endTestCase("Test03_LmGroupNest_Update"); 	
	}
	
/*	@Test
	public void Test04_LmGroupNest_Copy(ITestContext context) {
		
	}
	
	@Test
	public void Test05_LmGroupNest_Delete() {
		
	}*/
	
	@Test(dataProvider = "GroupNameData", dataProviderClass = DataProviderClass.class)
	public void Test06_NestGroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {
		
		Log.startTestCase("Test06_NestGroupNameValidation");
		
		JSONObject payload = JsonFileReader.updateLoadGroup(payloadfile, "name", groupName);
		ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup",payload);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
		
		Log.endTestCase("Test06_NestGroupNameValidation");
		
	}
	
	@Test(dataProvider = "KwCapacityData", dataProviderClass = DataProviderClass.class)
	public void Test07_NestKwCapacityValidation(Float kwCapacity, String expectedFieldCode, int expectedStatusCode) {
		
		Log.startTestCase("Test07_NestKwCapacityValidation");
		
		JSONObject payload = JsonFileReader.updateLoadGroup(payloadfile, "kWCapacity", kwCapacity.toString());
		ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup",payload);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
		
		Log.endTestCase("Test07_NestKwCapacityValidation");
	}
}
