package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.JsonUtil;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LoadGroupNestAPITest {

    MockLoadGroupBase loadGroup = null;

    @BeforeMethod
    public void setUp(Method method) {
        loadGroup = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_NEST);
    }

    @Test
    public void loadGroupNest_01_Create(ITestContext context) {

        Log.startTestCase("loadGroupNest_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path("groupId").toString();
        context.setAttribute("groupId", groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("GroupId should not be Null", groupId != null);
        Log.endTestCase("loadGroupNest_01_Create");
    }

    @Test(dependsOnMethods = { "loadGroupNest_01_Create" })
    public void loadGroupNest_02_Get(ITestContext context) {

        Log.startTestCase("loadGroupNest_02_Get");
        String groupId = context.getAttribute("groupId").toString();
        Log.info("GroupId of LmGroupNest created is : " + groupId);

        ExtractableResponse<?> response = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        MockLoadGroupBase nestLoadGroup = response.as(MockLoadGroupBase.class);
        context.setAttribute("Nest_GrpName", loadGroup.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(nestLoadGroup.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == nestLoadGroup.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(nestLoadGroup.getKWCapacity()));
   
        Boolean disableGroup = nestLoadGroup.isDisableGroup();
        assertTrue("Group Should be disabled : ", !disableGroup);
   
        Boolean disableControl = nestLoadGroup.isDisableControl();
        assertTrue("Control Should be disabled : ", !disableControl);
        Log.endTestCase("loadGroupNest_02_Get");
    }

    @Test(dependsOnMethods = {"loadGroupNest_01_Create"})
    public void loadGroupNest_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupNest_03_Update");

        String groupId = context.getAttribute("groupId").toString();
        String name = "Auto_LM_Group_Nest_Update2";
        context.setAttribute("Nest_UpdateGrpName", name);

        loadGroup.setId(Integer.valueOf(groupId));
        loadGroup.setKWCapacity(888.0);
        loadGroup.setName(name);

        Log.info("Updated payload is :" +  JsonUtil.beautifyJson(loadGroup.toString()));

        ExtractableResponse<?> response = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue("Status code should be 200", response.statusCode() == 200);

        ExtractableResponse<?> getUpdatedResponse = ApiCallHelper.get("getloadgroup", groupId);

        MockLoadGroupBase updatedNestLoadGroup = getUpdatedResponse.as(MockLoadGroupBase.class);
        assertTrue("Name Should be : " + name, name.equals(loadGroup.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == updatedNestLoadGroup.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(),
                   loadGroup.getKWCapacity().equals(updatedNestLoadGroup.getKWCapacity()));
        Log.endTestCase("loadGroupNest_03_Update");
    }

    @Test(enabled = false)
    public void loadGroupNest_04_Copy(ITestContext context) {

    }

    @Test(dependsOnMethods = { "loadGroupNest_01_Create" })
    public void loadGroupNest_05_Delete(ITestContext context) {

        Log.startTestCase("loadGroupNest_05_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder()
                                    .name(context.getAttribute("Nest_UpdateGrpName").toString())
                                    .build();
        Log.info("Delete payload is : " + JsonUtil.beautifyJson(lmDeleteObject.toString()));
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup", lmDeleteObject, context.getAttribute("groupId").toString());

        assertTrue("Status code should be 200", response.statusCode() == 200);
        Log.endTestCase("loadGroupNest_05_Delete");

    }
    
    @Test(dataProvider = "GroupNameData", dependsOnMethods = { "loadGroupNest_01_Create" })
    public void loadGroupNest_06_GroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {
        Log.startTestCase("loadGroupNest_06_GroupNameValidation");
        loadGroup.setName(groupName);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(error.getFieldErrors().get(0).getCode()));
        Log.endTestCase("loadGroupNest_06_GroupNameValidation");
    }

    @Test(dataProvider = "KwCapacityData", dependsOnMethods = { "loadGroupNest_01_Create" })
    public void loadGroupNest_07_KwCapacityValidation(Double kwCapacity, String expectedFieldCode, int expectedStatusCode) {
        Log.startTestCase("loadGroupNest_07_KwCapacityValidation");
        loadGroup.setKWCapacity(kwCapacity);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error",  error.getMessage().equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(error.getFieldErrors().get(0).getCode()));
        Log.endTestCase("loadGroupNest_07_KwCapacityValidation");
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
                /* { context.getAttribute("Nest_GrpName"), "Name must be unique.", 422 } */};
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
}
