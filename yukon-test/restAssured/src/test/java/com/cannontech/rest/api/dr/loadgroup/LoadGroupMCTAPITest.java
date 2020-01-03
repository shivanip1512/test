package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockAddressLevel;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupMCT;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class LoadGroupMCTAPITest {

    private static final int invalidRouteId = 2222222;

    @Test
    public void loadGroupMCT_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupMCT_01_Create");
        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID));
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID) != null, "Group Id should not be Null");
        loadGroup.setId(createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID));
        context.setAttribute("expectedloadGroup", loadGroup);
        Log.endTestCase("loadGroupMCT_01_Create");
    }

    @Test(dependsOnMethods = "loadGroupMCT_01_Create")
    public void loadGroupMCT_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupMCT_02_Get");
        Log.info("Group Id of LmGroupMCT created is : " + context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID));

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupMCT loadGroupResponse = getResponse.as(MockLoadGroupMCT.class);
        context.setAttribute("MCT_GrpName", loadGroupResponse.getName());

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) context.getAttribute("expectedloadGroup");

        assertTrue(loadGroup.getName().equals(loadGroupResponse.getName()), "Name Should be : " + loadGroup.getName());
        assertTrue(loadGroup.getType() == loadGroupResponse.getType(), "Type Should be : " + loadGroup.getType());
        assertTrue(loadGroup.getKWCapacity().equals(loadGroupResponse.getKWCapacity()),
                "kWCapacity Should be : " + loadGroup.getKWCapacity());
        assertTrue(loadGroup.isDisableGroup() == (loadGroupResponse.isDisableGroup()), "Group Should be disabled : ");
        assertTrue(loadGroup.isDisableControl() == (loadGroupResponse.isDisableControl()), "Control Should be disabled : ");

        Log.endTestCase("loadGroupMCT_02_Get");
    }

    @Test(dependsOnMethods = "loadGroupMCT_02_Get")
    public void loadGroupMCT_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupMCT_03_Update");

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) context.getAttribute("expectedloadGroup");
        String name = "LM_Group_MCT_Name_Update";
        loadGroup.setName(name);
        loadGroup.setKWCapacity(888.0);
        context.setAttribute("MCT_GrpName", name);

        Log.info("Updated Load Group is :" + loadGroup);
        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup",
                loadGroup,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        MockLoadGroupMCT updatedLoadGroupResponse = getupdatedResponse.as(MockLoadGroupMCT.class);
        assertTrue(name.equals(updatedLoadGroupResponse.getName()), "Name Should be : " + name);
        assertTrue(loadGroup.getType().equals(updatedLoadGroupResponse.getType()), "Type Should be : " + loadGroup.getType());
        assertTrue(loadGroup.getKWCapacity().equals(updatedLoadGroupResponse.getKWCapacity()),
                "kWCapacity Should be : " + loadGroup.getKWCapacity());
        Log.endTestCase("loadGroupMCT_03_Update");
    }

    @Test(dependsOnMethods = "loadGroupMCT_03_Update")
    public void loadGroupMCT_04_Copy(ITestContext context) {

        Log.startTestCase("loadGroupMCT_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_MCT)).build();
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(copyResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null, "Group Id should not be Null");
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupMCT loadGroupResponse = getResponse.as(MockLoadGroupMCT.class);
        context.setAttribute("Copied_MCT_GrpName", loadGroupResponse.getName());
        context.setAttribute("Copied_MCT_GrpId", loadGroupResponse.getId());
        Log.endTestCase("loadGroupMCT_04_Copy");
    }

    @Test(dependsOnMethods = "loadGroupMCT_04_Copy")
    public void loadGroupMCT_05_Delete(ITestContext context) {
        Log.startTestCase("loadGroupMCT_05_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("MCT_GrpName").toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", deleteResponse.statusCode() == 200);

        // Delete copy Load group
        lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Copied_MCT_GrpName").toString()).build();
        ExtractableResponse<?> deleteCopyResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute("Copied_MCT_GrpId").toString());
        assertTrue("Status code should be 200", deleteCopyResponse.statusCode() == 200);
        Log.startTestCase("loadGroupMCT_05_Delete");
    }

    /**
     * Negative validation when Load Group is created with same name used while creation of Control Load Group in
     * loadGroupMCT_01_Create
     */
    @Test(dependsOnMethods = "loadGroupMCT_01_Create")
    public void loadGroupMCT_06_Name_Is_Same_Validation(ITestContext context) {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setName(loadGroup.getName());
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name must be unique."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Load Group is copied with invalid Route Id
     */
    @Test(dependsOnMethods = "loadGroupMCT_01_Create")
    public void loadGroupMCT_18_CopyWithInvalidRouteId(ITestContext context) {

        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_MCT)).build();
        loadGroupCopy.setRouteId(invalidRouteId);
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup", loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(copyResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(copyResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(copyResponse, "routeId", "Route Id does not exist."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Load Group name field is passed as blank while creation of Load Group
     */
    @Test
    public void loadGroupMCT_07_NameAsBlankValidation(ITestContext context) {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setName("");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Load Group name is passed with special characters | , / while creation of Load Group
     */
    @Test
    public void loadGroupMCT_08_NameWithSpecialCharactersValidation(ITestContext context) {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setName("Test\\,MCT");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "name",
                        "Cannot be blank or include any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Load Group name is passed with more than 60 characters while creation of Load Group
     */
    @Test
    public void loadGroupMCT_09_NameWithMoreThanSixtyCharactersValidation(ITestContext context) {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setName("TestLoadGroupName_MoreThanSixtyCharacter_TestLoadGroupNames>60");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Exceeds maximum length of 60."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Invalid Route Id is passed while creation of Load Group
     */
    @Test
    public void loadGroupMCT_10_InvalidRouteIdValidation(ITestContext context) {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setRouteId(2999999);
        ;
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "routeId", "Route Id does not exist."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Kw Capacity field is passed as blank while creation of MCT load group
     */
    @Test
    public void loadGroupMCT_11_KwCapacityAsBlankValidation() {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setKWCapacity(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "kWCapacity", "kW Capacity is required."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Kw Capacity field value is less than the minimum value allowed while creation of
     * MCT load group
     */
    @Test
    public void loadGroupMCT_12_KwCapacityLessThanMinValueValidation() {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setKWCapacity(-222.0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "kWCapacity", "Must be between 0 and 99,999.999."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Kw Capacity field value is greater than the Maximum value allowed while creation of
     * MCT load group
     */
    @Test
    public void loadGroupMCT_13_KwCapacityGreaterThanMaxValueValidation() {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setKWCapacity(100000.0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "kWCapacity", "Must be between 0 and 99,999.999."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Load Group address field is passed as blank while creation of Load Group
     */
    @Test
    public void loadGroupMCT_14_AddressAsBlankValidation(ITestContext context) {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setAddress(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "address", "Address is required."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Load Group address field is passed as blank while creation of Load Group
     */
    @Test
    public void loadGroupMCT_15_AddressLessThanOneValidation(ITestContext context) {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setAddress(-1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "address", "Must be between 1 and 2,147,483,647."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Load Group MCT Address field is passed as blank while creation of Load Group
     */
    @Test
    public void loadGroupMCT_16_MCTAddressAsBlankValidation(ITestContext context) {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setLevel(MockAddressLevel.MCT_ADDRESS);
        loadGroup.setMctDeviceId(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "mctDeviceId", "MCT Address is required."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Load Group MCT Address field is passed as blank while creation of Load Group
     */
    @Test
    public void loadGroupMCT_17_InvalidMCTAddressValidation(ITestContext context) {

        MockLoadGroupMCT loadGroup = (MockLoadGroupMCT) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_MCT);
        loadGroup.setLevel(MockAddressLevel.MCT_ADDRESS);
        loadGroup.setMctDeviceId(2121212);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "mctDeviceId", "MCT device Id does not exist."),
                "Expected code in response is not correct");
    }
}
