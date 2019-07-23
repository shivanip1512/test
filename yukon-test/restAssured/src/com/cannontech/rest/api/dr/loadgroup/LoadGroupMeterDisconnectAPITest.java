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



public class LoadGroupMeterDisconnectAPITest {
	String LGBase = "LM_GROUP_METER_DISCONNECT";
	String type = "LM_GROUP_METER_DISCONNECT";
	String payloadfile = "loadgroup\\lmGroupMeterDisconnectCreate.json";

	 @Test(priority = 0)
	 public void Test01_LmGroupMeterDisconnect_Create(ITestContext context) {
    	Log.startTestCase("LmGroupMeterDisconnect_Create");
    	ExtractableResponse<?> createResponse =
                ApiCallHelper.post("saveloadgroup", "loadgroup\\lmGroupMeterDisconnectCreate.json");
    	String groupId = createResponse.path("groupId").toString();
    	context.setAttribute("MeterDisconnectgroupId", groupId);
    	assertTrue("Status code should be 200", createResponse.statusCode() == 200);
    	assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("LmGroupMeterDisconnect_Create");
    }
	 
	 @Test(priority = 1)
	 public void Test02_LmGroupMeterDisconnect_Get(ITestContext context) {
		Log.startTestCase("Test02_LmGroupMeterDisconnect_Get");    
			String groupId = context.getAttribute("MeterDisconnectgroupId").toString();
			
	    	JSONObject jo = JsonFileReader.readJsonFileAsJSONObject(payloadfile);
			JsonPath jp = new JsonPath(jo.toJSONString());
	    	Float kWCapacity = jp.getFloat(LGBase+".kWCapacity"); 
	    	String name = jp.get(LGBase+".name");  	
	    	
	    	Log.info("GroupId of LmGroupMeterDisconnect created is : "+groupId);
	    	
	        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
	        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

	        JsonPath jsonPath = getResponse.jsonPath();
	        context.setAttribute("MR_GrpName", jsonPath.get(LGBase+".name"));

	        assertTrue("Name Should be : "+name, name.equals((String) jsonPath.get(LGBase+".name")));
	        assertTrue("Type Should be : "+type, type.equals(jsonPath.get(LGBase+".type")));
	        assertTrue("kWCapacity Should be : "+kWCapacity, kWCapacity.equals(jsonPath.get(LGBase+".kWCapacity")));
	        boolean disableGroup = (boolean) jsonPath.get(LGBase+".disableGroup");
	        assertTrue("Group Should be disabled : ", !disableGroup);
	        boolean disableControl = (boolean) jsonPath.get(LGBase+".disableControl");
	        assertTrue("Control Should be disabled : ", !disableControl);
	        Log.endTestCase("Test02_LmGroupMeterDisconnect_Get");
	    }
	
	 @Test(priority = 2)
	 public void Test03_LmGroupMeterDisconnect_Update(ITestContext context) {
		 Log.startTestCase("Test03_LmGroupMeterDisconnect_Update");    
	    	
	    	String groupId = context.getAttribute("MeterDisconnectgroupId").toString();
	    	Float kWCapacity = (float) 888.766;
	    	String name = "Test Meter Disconnect_Update";
	    	context.setAttribute("MR_GrpName", name);
	    	
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
	        Log.endTestCase("Test03_LmGroupMeterDisconnect_Update"); 
		 
		}
}

