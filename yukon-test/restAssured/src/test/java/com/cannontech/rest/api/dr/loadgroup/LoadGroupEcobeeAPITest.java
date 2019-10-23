
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
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupEcobee;
import com.cannontech.rest.api.utilities.Log;

import io.restassured.response.ExtractableResponse;

public class LoadGroupEcobeeAPITest {
    MockLoadGroupEcobee loadGroup = null;

    @BeforeClass
    public void setUp() {
        loadGroup = (MockLoadGroupEcobee) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ECOBEE);
    }

    /**
     * This test case validates creation of Ecobee load group with default values provided
     */

    @Test
    public void loadGroupEcobee_01_Create(ITestContext context) {
        String paoId = null;
        Log.startTestCase("loadGroupEcobee_01_Create");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        paoId = createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString();
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, paoId);
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", paoId != null);
        Log.endTestCase("loadGroupEcobee_01_Create");
    }

    /**
     * This test case validates retrieval(Get) of Ecobee load group created via loadGroupEcobee_01_Create
     */

    @Test(dependsOnMethods = "loadGroupEcobee_01_Create")
    public void loadGroupEcobee_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupEcobee_02_Get");
        Log.info("Group Id of LmGroupEcobee created is : " + context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup", context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        MockLoadGroupEcobee loadGroupResponse = getResponse.as(MockLoadGroupEcobee.class);
        context.setAttribute("Ecobee_GrpName", loadGroupResponse.getName());
        assertTrue("Name Should be : " + loadGroup.getName(), loadGroup.getName().equals(loadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == loadGroupResponse.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(loadGroupResponse.getKWCapacity()));
        Boolean disableGroup = loadGroupResponse.isDisableGroup();
        assertTrue("Group Should be disabled : ", !disableGroup);
        Boolean disableControl = loadGroupResponse.isDisableControl();
        assertTrue("Control Should be disabled : ", !disableControl);
        Log.endTestCase("loadGroupEcobee_02_Get");
    }

    /**
     * This test case validates updation of Ecobee load group created via loadGroupEcobee_01_Create
     */

    @Test(dependsOnMethods = "loadGroupEcobee_01_Create")
    public void loadGroupEcobee_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupEcobee_03_Update");
        String name = "LM_Group_Ecobee_Name_Update";
        loadGroup.setName(name);
        loadGroup.setKWCapacity(888.0);
        context.setAttribute("Ecobee_GrpName", name);
        Log.info("Updated Load Group is :" + loadGroup);
        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup",
                                                                loadGroup,
                                                                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);
        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup", context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        MockLoadGroupEcobee updatedLoadGroupResponse = getupdatedResponse.as(MockLoadGroupEcobee.class);
        assertTrue("Name Should be : " + name, name.equals(updatedLoadGroupResponse.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType().equals(updatedLoadGroupResponse.getType()));
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(), loadGroup.getKWCapacity().equals(updatedLoadGroupResponse.getKWCapacity()));
        Log.endTestCase("loadGroupEcobee_03_Update");
    }

    /**
     * This test case validates copy of Ecobee load group via loadGroupEcobee_01_Create
     */

    @Test(dependsOnMethods = "loadGroupEcobee_01_Create")
    public void loadGroupEcobee_04_Copy(ITestContext context) {
        Log.startTestCase("loadGroupEcobee_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder().name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_ECOBEE)).build();
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                                                                 loadGroupCopy,
                                                                 context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID) != null);
        context.setAttribute("copiedLoadGroupName", loadGroupCopy.getName());
        Log.endTestCase("loadGroupEcobee_04_Copy");
    }

    @Test(dependsOnMethods = "loadGroupEcobee_01_Create", dataProvider = "GroupNameData")
    public void loadGroupEcobee_05_GroupNameValidation(String groupName, String expectedFieldCode, int expectedStatusCode) {
        Log.startTestCase("loadGroupEcobee_05_GroupNameValidation");
        loadGroup.setName(groupName);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(code));
        Log.endTestCase("loadGroupEcobee_05_GroupNameValidation");
    }

    @Test(dependsOnMethods = "loadGroupEcobee_01_Create", dataProvider = "KwCapacityData")
    public void loadGroupEcobee_06_KwCapacityValidation(Double kwCapacity, String expectedFieldCode, int expectedStatusCode) {
        Log.startTestCase("loadGroupEcobee_06_KwCapacityValidation");
        loadGroup.setKWCapacity(kwCapacity);
        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(1).getCode();
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(code));
        Log.endTestCase("loadGroupEcobee_06_KwCapacityValidation");
    }

    /**
     * This test case validates deletion of Ecobee load group with default values provided
     */

    @Test(dependsOnMethods = "loadGroupEcobee_01_Create")
    public void loadGroupEcobee_07_Delete(ITestContext context) {
        String expectedMessage = "Id not found";
        Log.startTestCase("loadGroupEcobee_07_Delete");
        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Ecobee_GrpName").toString()).build();
        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteloadgroup",
                                                                     lmDeleteObject,
                                                                     context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", deleteResponse.statusCode() == 200);

        // Get request to validate load group is deleted
        ExtractableResponse<?> deleteResponseValidation = ApiCallHelper.get("getloadgroup", context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 400", deleteResponseValidation.statusCode() == 400);

        MockApiError error = deleteResponseValidation.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + expectedMessage, expectedMessage.equals(error.getMessage()));

        Log.endTestCase("loadGroupEcobee_07_Delete");

    }

    /**
     * DataProvider provides data to test method in the form of object array Data provided - col1 : Group Name col2 :
     * Expected field errors code in response col3 : Expected response code
     */
    @DataProvider(name = "GroupNameData")
    public Object[][] getGroupNameData(ITestContext context) {

        return new Object[][] { { "", "Name is required.", 422 },
                { "Test\\Ecobee", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "Test,Ecobee", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "TestEcobeeMoreThanSixtyCharacter_TestEcobeeMoreThanSixtyCharacters", "Exceeds maximum length of 60.", 422 },
                { context.getAttribute("Ecobee_GrpName"), "Name must be unique.", 422 } };
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
