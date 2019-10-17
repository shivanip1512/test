package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupEcobee;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupEcoBeeAPITest {
    MockLoadGroupEcobee loadGroup = null;
    public String copyPaoId = null;
    public String PaoId = null;

    @BeforeMethod
    public void setUp(Method method) {
        loadGroup = (MockLoadGroupEcobee) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ECOBEE);
    }

    @Test
    public void loadGroupEcoBee_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupEcoBee_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        PaoId = createResponse.path("groupId").toString();
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", PaoId != null);
        context.setAttribute("EcoBee_GrpName", loadGroup.getName());
        Log.endTestCase("loadGroupEcoBee_01_Create");
    }

    @Test(dependsOnMethods = { "loadGroupEcoBee_01_Create" })
    public void loadGroupEcoBee_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupEcoBee_02_Get");

        Log.info("Load Group Id of LmGroupItron created is : " + PaoId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", PaoId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupEcobee loadGroupResponse = getResponse.as(MockLoadGroupEcobee.class);
        context.setAttribute("EcoBee_GrpName", loadGroupResponse.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(loadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == loadGroupResponse.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(loadGroupResponse.getKWCapacity()));
        Boolean disableGroup = loadGroupResponse.isDisableGroup();
        assertTrue("Group Should be disabled : ", !disableGroup);
        Boolean disableControl = loadGroupResponse.isDisableControl();
        assertTrue("Control Should be disabled : ", !disableControl);
        Log.endTestCase("loadGroupEcoBee_02_Get");
    }

    @Test(dependsOnMethods = { "loadGroupEcoBee_01_Create" })
    public void loadGroupEcoBee_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupEcoBee_03_Update");

        String name = "LM_Group_EcoBee_Name_Update";

        loadGroup.setName(name);
        loadGroup.setKWCapacity(888.0);
        context.setAttribute("EcoBee_GrpName", name);

        Log.info("Updated Load Group is :" + loadGroup);
        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", loadGroup, PaoId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", PaoId);
        MockLoadGroupEcobee updatedLoadGroupResponse = getupdatedResponse.as(MockLoadGroupEcobee.class);
        assertTrue("Name Should be : " + name, name.equals(updatedLoadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType().equals(updatedLoadGroupResponse.getType()));
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(updatedLoadGroupResponse.getKWCapacity()));
        Log.endTestCase("loadGroupEcoBee_03_Update");
    }

    @Test(dependsOnMethods = { "loadGroupEcoBee_01_Create" })
    public void loadGroupEcoBee_04_Copy(ITestContext context) {
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder().name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_ECOBEE)).build();
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup", loadGroupCopy, PaoId);
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
        assertTrue("PAO ID should not be Null", copyResponse.path("groupId") != null);
        context.setAttribute("copiedProgramName", loadGroupCopy.getName());
        copyPaoId = copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

    }

    @Test(dependsOnMethods = { "loadGroupEcoBee_01_Create" })
    public void loadGroupEcoBee_05_Delete(ITestContext context) {

        String expectedMessage = "Id not found";

        Log.startTestCase("loadGroupEcoBee_05_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("EcoBee_GrpName").toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup", lmDeleteObject, PaoId.toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // for copied load group
        MockLMDto lmDeleteObject1 = MockLMDto.builder().name(context.getAttribute("copiedProgramName").toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject1);
        ExtractableResponse<?> response1 = ApiCallHelper.delete("deleteloadgroup", lmDeleteObject1, copyPaoId);
        assertTrue("Status code should be 200", response1.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> response2 = ApiCallHelper.get("getloadgroup", PaoId.toString());
        assertTrue("Status code should be 400", response2.statusCode() == 400);

        MockApiError error = response2.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));

        Log.endTestCase("loadGroupEcoBee_05_Delete");

    }

    @Test(dataProvider = "GroupNameData")
    public void loadGroupEcoBee_06_GroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadGroupItron_06_GroupNameValidation");
        loadGroup.setName(groupName);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
        Log.endTestCase("loadGroupItron_06_GroupNameValidation");
    }

    @Test(dataProvider = "KwCapacityData")
    public void loadGroupEcoBee_07_KwCapacityValidation(Double kwCapacity, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadGroupItron_07_KwCapacityValidation");
        loadGroup.setKWCapacity(kwCapacity);
        loadGroup.setName("KwCapacityValidation");
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));
        Log.endTestCase("loadGroupItron_0y_KwCapacityValidation");
    }

    /**
     * DataProvider provides data to test method in the form of object array Data provided - col1 : Group Name col2 :
     * Expected field errors code in response col3 : Expected response code
     */
    @DataProvider(name = "GroupNameData")
    public Object[][] getGroupNameData(ITestContext context) {

        return new Object[][] { { "", "Name is required.", 422 },
                { "Test\\Nest", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "Test,Nest", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "TestNestMoreThanSixtyCharacter_TestNestMoreThanSixtyCharacters", "Exceeds maximum length of 60.", 422 },
                { context.getAttribute("EcoBee_GrpName"), "Name must be unique.", 422 } };
    }

    /**
     * DataProvider provides data to test method in the form of object array Data provided - col1 : KwCapacity col2 :
     * Expected field errors code in response col3 : Expected response code
     */
    @DataProvider(name = "KwCapacityData")
    public Object[][] getKwCapacityData() {

        return new Object[][] {
                // {(float) , "kW Capacity is required.", 422 },
                { -222.0, "Must be between 0 and 99,999.999.", 422 }, { 100000.0, "Must be between 0 and 99,999.999.", 422 } };
    }
}
