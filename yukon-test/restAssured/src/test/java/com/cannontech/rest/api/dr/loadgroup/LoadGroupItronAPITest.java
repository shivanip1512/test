package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupItron;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;

public class LoadGroupItronAPITest {
    MockLoadGroupItron loadGroup = null;

    @BeforeClass
    public void setUp() {
        loadGroup = (MockLoadGroupItron) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ITRON);
    }

    @Test
    public void loadGroupItron_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupItron_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", groupId != null);
        Log.endTestCase("loadGroupItron_01_Create");
    }

    @Test(dependsOnMethods = { "loadGroupItron_01_Create" })
    public void loadGroupItron_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupItron_02_Get");
        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

        Log.info("Group Id of LmGroupItron created is : " + groupId);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupItron loadGroupResponse = getResponse.as(MockLoadGroupItron.class);
        context.setAttribute("Itron_GrpName", loadGroupResponse.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(loadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == loadGroupResponse.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(),
                loadGroup.getKWCapacity().equals(loadGroupResponse.getKWCapacity()));
        Boolean disableGroup = loadGroupResponse.isDisableGroup();
        assertTrue("Group Should be disabled : ", !disableGroup);
        Boolean disableControl = loadGroupResponse.isDisableControl();
        assertTrue("Control Should be disabled : ", !disableControl);
        assertTrue("VirtualRelayId Should be : " + loadGroup.getVirtualRelayId(),
                loadGroup.getVirtualRelayId().equals(loadGroupResponse.getVirtualRelayId()));
        Log.endTestCase("loadGroupItron_02_Get");
    }

    @Test(dependsOnMethods = { "loadGroupItron_01_Create" })
    public void loadGroupItron_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupItron_03_Update");
        String groupId = context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString();

        String name = "LM_Group_Itron_Name_Update";
        loadGroup.setVirtualRelayId(7);
        loadGroup.setName(name);
        loadGroup.setKWCapacity(888.0);
        context.setAttribute("Itron_GrpName", name);

        Log.info("Updated Load Group is :" + loadGroup);

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", loadGroup, groupId);
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", groupId);
        assertTrue("Status code should be 200", getupdatedResponse.statusCode() == 200);

        MockLoadGroupItron updatedLoadGroupResponse = getupdatedResponse.as(MockLoadGroupItron.class);
        assertTrue("Name Should be : " + name, name.equals(updatedLoadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType().equals(updatedLoadGroupResponse.getType()));
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(),
                loadGroup.getKWCapacity().equals(updatedLoadGroupResponse.getKWCapacity()));
        assertTrue("Virtual Relay Id Should be : " + loadGroup.getVirtualRelayId(),
                loadGroup.getVirtualRelayId().equals(updatedLoadGroupResponse.getVirtualRelayId()));
        Log.endTestCase("loadGroupItron_03_Update");
    }

    /**
     * This test case validates copy of Itron load group
     */

    @Test(dependsOnMethods = { "loadGroupItron_01_Create" })
    public void loadGroupItron_04_Copy(ITestContext context) {

        Log.startTestCase("loadGroupItron_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_ITRON)).build();

        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        String copyPaoId = copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", copyPaoId != null);
        context.setAttribute("Itron_CopyGrpId", copyPaoId);
        context.setAttribute("Itron_CopyGrpName", loadGroupCopy.getName());
        Log.endTestCase("loadGroupItron_04_Copy");
    }

    @Test(dataProvider = "GroupNameData", dependsOnMethods = "loadGroupItron_01_Create")
    public void loadGroupItron_05_GroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadGroupItron_05_GroupNameValidation");

        loadGroup.setName(groupName);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));

        Log.endTestCase("loadGroupItron_05_GroupNameValidation");
    }

    @Test(dataProvider = "KwCapacityData", dependsOnMethods = "loadGroupItron_01_Create")
    public void loadGroupItron_06_KwCapacityValidation(Double kwCapacity, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadGroupItron_06_KwCapacityValidation");

        loadGroup.setKWCapacity(kwCapacity);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[1]")));

        Log.endTestCase("loadGroupItron_06_KwCapacityValidation");
    }

    @Test(dataProvider = "VirtualRelayIdData", dependsOnMethods = "loadGroupItron_01_Create")
    public void loadGroupItron_07_VirtualRelayIdValidation(Integer virtualRelayId, String expectedFieldCode,
            int expectedStatusCode) {

        Log.startTestCase("loadGroupItron_07_VirtualRelayIdValidation");

        loadGroup.setVirtualRelayId(virtualRelayId);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Expected message should be - Validation error", jsonPath.get("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(jsonPath.get("fieldErrors.code[0]")));

        Log.endTestCase("loadGroupItron_07_VirtualRelayIdValidation");
    }

    /**
     * This test case validates deletion of Itron load group
     */

    @Test(dependsOnMethods = { "loadGroupItron_02_Get" })
    public void loadGroupItron_08_Delete(ITestContext context) {

        String expectedMessage = "Id not found";
        String grpToDelete = "Itron_GrpName";
        Log.startTestCase("loadGroupItron_08_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute(grpToDelete).toString()).build();
        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 400", getDeletedResponse.statusCode() == 400);

        MockApiError error = getDeletedResponse.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));

        // Delete copy Load group
        lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Itron_CopyGrpName").toString()).build();
        ExtractableResponse<?> deleteCopyResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute("Itron_CopyGrpId").toString());
        assertTrue("Status code should be 200", deleteCopyResponse.statusCode() == 200);

        Log.endTestCase("loadGroupItron_08_Delete");
    }

    /**
     * DataProvider provides data to test method in the form of object array Data provided - col1 : Group Name col2 :
     * Expected field errors code in response col3 : Expected response code
     */
    @DataProvider(name = "GroupNameData")
    public Object[][] getGroupNameData(ITestContext context) {

        return new Object[][] { { "", "Name is required.", 422 },
                { "Test\\Itron", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "Test,Itron", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "TestItronMoreThanSixtyCharacter_TestNestMoreThanSixtyCharacters", "Exceeds maximum length of 60.", 422 },
                { context.getAttribute("Itron_GrpName"), "Name must be unique.", 422 } };
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

    /**
     * DataProvider provides data to test method in the form of object array Data provided - col1 : VirtualRelayIdData
     * col2 : Expected field errors code in response col3 : Expected response code
     */
    @DataProvider(name = "VirtualRelayIdData")
    public Object[][] getVirtualRelayIdData() {

        return new Object[][] {
                // { "", "Virtual RelayId is required.", 422 },
                { -2, "Must be between 1 and 8.", 422 }, { 11, "Must be between 1 and 8.", 422 } };
    }

}
