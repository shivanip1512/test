package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockApiFieldError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupHoneywell;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LoadGroupHoneywellAPITest {

    MockLoadGroupHoneywell loadGroup = null;

    @BeforeClass
    public void setUp() {
        loadGroup = (MockLoadGroupHoneywell) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_HONEYWELL);
    }

    /**
     * This test case validates creation of Honeywell load group with default values
     */
    @Test
    public void loadGroupHoneywell_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupHoneywell_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        String groupId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, groupId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", groupId != null);
        Log.endTestCase("loadGroupHoneywell_01_Create");
    }

    /**
     * This test case validates fields of Honeywell load group created via loadGroupHoneywell_01_Create
     */
    @Test(dependsOnMethods = "loadGroupHoneywell_01_Create")
    public void loadGroupHoneywell_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupHoneywell_02_Get");
        Log.info(
                "Group Id of LmGroupHoneywell created is : " + context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupHoneywell loadGroupResponse = getResponse.as(MockLoadGroupHoneywell.class);
        context.setAttribute("Honeywell_GrpName", loadGroupResponse.getName());

        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(loadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == loadGroupResponse.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(),
                loadGroup.getKWCapacity().equals(loadGroupResponse.getKWCapacity()));
        Boolean disableGroup = loadGroupResponse.isDisableGroup();
        assertTrue("Group Should be disabled : ", !disableGroup);
        Boolean disableControl = loadGroupResponse.isDisableControl();
        assertTrue("Control Should be disabled : ", !disableControl);
        Log.endTestCase("loadGroupHoneywell_02_Get");
    }

    /**
     * This test case updates name of Honeywell load group created via loadGroupHoneywell_01_Create
     */
    @Test(dependsOnMethods = "loadGroupHoneywell_01_Create")
    public void loadGroupHoneywell_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupHoneywell_03_Update");

        String name = "LM_Group_Honeywell_Name_Update";
        loadGroup.setName(name);
        loadGroup.setKWCapacity(888.0);
        context.setAttribute("Honeywell_GrpName", name);

        Log.info("Updated Load Group is :" + loadGroup);
        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup",
                loadGroup,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        MockLoadGroupHoneywell updatedLoadGroupResponse = getupdatedResponse.as(MockLoadGroupHoneywell.class);
        assertTrue("Name Should be : " + name, name.equals(updatedLoadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType().equals(updatedLoadGroupResponse.getType()));
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(),
                loadGroup.getKWCapacity().equals(updatedLoadGroupResponse.getKWCapacity()));
        Log.endTestCase("loadGroupHoneywell_03_Update");
    }

    /**
     * This test case copies Honeywell load group created via loadGroupHoneywell_01_Create
     */
    @Test(dependsOnMethods = "loadGroupHoneywell_01_Create")
    public void loadGroupHoneywell_04_Copy(ITestContext context) {
        Log.startTestCase("loadGroupHoneywell_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getLoadGroupName(MockPaoType.LM_GROUP_HONEYWELL)).build();
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
        String copyGroupId = copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        assertTrue("Group Id should not be Null", copyGroupId != null);
        context.setAttribute("honeywell_CopyGrpId", copyGroupId);
        context.setAttribute("honeywell_CopyGrpName", loadGroupCopy.getName());
        Log.endTestCase("loadGroupHoneywell_04_Copy");
    }

    /**
     * This test case did all the negative validation required for Group name field while creation of Honeywell load
     * group
     */
    @Test(dependsOnMethods = "loadGroupHoneywell_01_Create", dataProvider = "GroupNameData")
    public void loadGroupHoneywell_05_GroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadGroupHoneywell_05_GroupNameValidation");

        loadGroup.setName(groupName);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(code));

        Log.endTestCase("loadGroupHoneywell_05_GroupNameValidation");
    }

    /**
     * This test case did all the negative validation required for Kw Capacity field while creation of Honeywell load
     * group
     */
    @Test(dependsOnMethods = "loadGroupHoneywell_01_Create", dataProvider = "KwCapacityData")
    public void loadGroupHoneywell_06_KwCapacityValidation(Double kwCapacity, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadGroupHoneywell_06_KwCapacityValidation");

        loadGroup.setKWCapacity(kwCapacity);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(1).getCode();
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(code));

        Log.endTestCase("loadGroupHoneywell_06_KwCapacityValidation");
    }

    /**
     * This test case deletes Honeywell load group created via loadGroupHoneywell_01_Create
     */
    @Test(dependsOnMethods = "loadGroupHoneywell_02_Get")
    public void loadGroupHoneywell_07_Delete(ITestContext context) {
        Log.startTestCase("loadGroupHoneywell_07_Delete");
        String expectedMessage = "Id not found";

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Honeywell_GrpName").toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", deleteResponse.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedLoadGroupResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 400", getDeletedLoadGroupResponse.statusCode() == 400);
        MockApiError error = getDeletedLoadGroupResponse.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));

        // Delete copy Load group
        lmDeleteObject = MockLMDto.builder().name(context.getAttribute("honeywell_CopyGrpName").toString()).build();
        ExtractableResponse<?> deleteCopyResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute("honeywell_CopyGrpId").toString());
        assertTrue("Status code should be 200", deleteCopyResponse.statusCode() == 200);
        Log.startTestCase("loadGroupHoneywell_07_Delete");
    }

    /**
     * DataProvider provides data to test method in the form of object array Data provided - col1 : Group Name col2 :
     * Expected field errors code in response col3 : Expected response code
     */
    @DataProvider(name = "GroupNameData")
    public Object[][] getGroupNameData(ITestContext context) {

        return new Object[][] { { "", "Name is required.", 422 },
                { "Test\\Honeywell", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "Test,Honeywell", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "TestHoneywellMoreThanSixtyCharacter_TestHoneywellMoreThanSixtyCharacters", "Exceeds maximum length of 60.",
                        422 },
                { context.getAttribute("Honeywell_GrpName"), "Name must be unique.", 422 } };
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