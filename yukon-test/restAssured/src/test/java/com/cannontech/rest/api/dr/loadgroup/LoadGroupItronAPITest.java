package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupItron;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupItronAPITest {
    MockLoadGroupItron loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        loadGroup = (MockLoadGroupItron) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ITRON);
    }

    @Test
    public void loadGroupItron_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupItron_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path("groupId").toString();
        context.setAttribute("groupId", groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("GROUP ID should not be Null", groupId != null);
        Log.endTestCase("loadGroupItron_01_Create");
    }

    @Test
    public void loadGroupItron_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupItron_02_Get");
        String groupId = context.getAttribute("groupId").toString();

        Log.info("GroupId of LmGroupItron created is : " + groupId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupItron loadGroupResponse = getResponse.as(MockLoadGroupItron.class);
        context.setAttribute("Itron_GrpName", loadGroupResponse.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(loadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == loadGroupResponse.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(loadGroupResponse.getKWCapacity()));
        Boolean disableGroup = loadGroupResponse.isDisableGroup();
        assertTrue("Group Should be disabled : ", !disableGroup);
        Boolean disableControl = loadGroupResponse.isDisableControl();
        assertTrue("Control Should be disabled : ", !disableControl);
        assertTrue("VirtualRelayId Should be : " + loadGroup.getVirtualRelayId(), loadGroup.getVirtualRelayId().equals(loadGroupResponse.getVirtualRelayId()));
        Log.endTestCase("loadGroupItron_02_Get");
    }

    @Test
    public void loadGroupItron_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupItron_03_Update");
        String groupId = context.getAttribute("groupId").toString();

        String name = "LM_Group_Itron_Name_Update";
        loadGroup.setVirtualRelayId(7);
        loadGroup.setName(name);
        loadGroup.setKWCapacity(888.0);
        context.setAttribute("Itron_GrpName", name);

        Log.info("Updated payload is :" + loadGroup);

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        MockLoadGroupItron updatedLoadGroupResponse = getupdatedResponse.as(MockLoadGroupItron.class);
        assertTrue("Name Should be : " + name, name.equals(updatedLoadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType().equals(updatedLoadGroupResponse.getType()));
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(updatedLoadGroupResponse.getKWCapacity()));
        assertTrue("Virtual Relay Id Should be : " + loadGroup.getVirtualRelayId(),
                   loadGroup.getVirtualRelayId().equals(updatedLoadGroupResponse.getVirtualRelayId()));
        Log.endTestCase("loadGroupItron_03_Update");
    }
    
    /*@Test
    public void Test04_LmGroupNest_Copy(ITestContext context) {
        
    }
    
    @Test
    public void Test05_LmGroupNest_Delete() {
        
    }
    */
    
    @Test(dataProvider = "GroupNameData")
    public void loadGroupItron_06_GroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {
        
        Log.startTestCase("loadGroupItron_06_GroupNameValidation");
        
        loadGroup.setName(groupName);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup",loadGroup);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
        
        Log.endTestCase("loadGroupItron_06_GroupNameValidation");
    }
    
    @Test(dataProvider = "KwCapacityData")
    public void loadGroupItron_07_KwCapacityValidation(Double kwCapacity, String expectedFieldCode, int expectedStatusCode) {
        
        Log.startTestCase("loadGroupItron_07_KwCapacityValidation");
        
        loadGroup.setKWCapacity(kwCapacity);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup",loadGroup);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
        
        Log.endTestCase("loadGroupItron_0y_KwCapacityValidation");
    }
    
    @Test(dataProvider = "VirtualRelayIdData")
    public void loadGroupItron_08_VirtualRelayIdValidation(Integer virtualRelayId, String expectedFieldCode, int expectedStatusCode) {
        
        Log.startTestCase("loadGroupItron_08_VirtualRelayIdValidation");
        
        loadGroup.setVirtualRelayId(virtualRelayId);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be "+expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
        
        Log.endTestCase("loadGroupItron_08_VirtualRelayIdValidation");
    }
    
    /**
     * DataProvider provides data to test method in the form of object array
     * Data provided -
     * col1 : Group Name
     * col2 : Expected field errors code in response
     * col3 : Expected response code
     */
    @DataProvider(name = "GroupNameData")
    public Object[][] getGroupNameData(ITestContext context) {

        return new Object[][] { { "", "Name is required.", 422 },
            { "Test\\Nest", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
            { "Test,Nest", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
            { "TestNestMoreThanSixtyCharacter_TestNestMoreThanSixtyCharacters", "Exceeds maximum length of 60.", 422 },
            { context.getAttribute("Itron_GrpName"), "Name must be unique.", 422 } };
    }

    /**
     * DataProvider provides data to test method in the form of object array
     * Data provided -
     * col1 : KwCapacity
     * col2 : Expected field errors code in response
     * col3 : Expected response code
     */
    @DataProvider(name = "KwCapacityData")
    public Object[][] getKwCapacityData() {

        return new Object[][] {
            // {(float) , "kW Capacity is required.", 422 },
            { -222.0, "Must be between 0 and 99,999.999.", 422 },
            { 100000.0, "Must be between 0 and 99,999.999.", 422 } };
    }

    /**
     * DataProvider provides data to test method in the form of object array
     * Data provided -
     * col1 : VirtualRelayIdData
     * col2 : Expected field errors code in response
     * col3 : Expected response code
     */
    @DataProvider(name = "VirtualRelayIdData")
    public Object[][] getVirtualRelayIdData() {

        return new Object[][] { 
        //{ "", "Virtual RelayId  is required.", 422 },
        { -2, "Must be between 1 and 8.", 422 }, 
        { 11, "Must be between 1 and 8.", 422 } };
    }

}
