package com.cannontech.rest.api.dr.loadgroup;

import static org.testng.Assert.assertTrue;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupPoint;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class LoadGroupPointAPITest {

    @Test
    public void loadGroupPoint_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupPoint_01_Create");
        MockLoadGroupPoint loadGroup = (MockLoadGroupPoint) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_POINT);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID));
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID) != null, "Group Id should not be Null");
        loadGroup.setId(createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID));
        context.setAttribute("expectedloadGroup", loadGroup);
        Log.endTestCase("loadGroupPoint_01_Create");
    }

    @Test(dependsOnMethods = "loadGroupPoint_01_Create")
    public void loadGroupPoint_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupPoint_02_Get");
        Log.info("Group Id of LmGroupPoint created is : " + context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID));

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupPoint loadGroupResponse = getResponse.as(MockLoadGroupPoint.class);
        context.setAttribute("Point_GrpName", loadGroupResponse.getName());

        MockLoadGroupPoint loadGroup = (MockLoadGroupPoint) context.getAttribute("expectedloadGroup");

        assertTrue(loadGroup.getName().equals(loadGroupResponse.getName()), "Name Should be : " + loadGroup.getName());
        assertTrue(loadGroup.getType() == loadGroupResponse.getType(), "Type Should be : " + loadGroup.getType());
        assertTrue(loadGroup.getKWCapacity().equals(loadGroupResponse.getKWCapacity()),
                "kWCapacity Should be : " + loadGroup.getKWCapacity());
        assertTrue(loadGroup.isDisableGroup() == (loadGroupResponse.isDisableGroup()), "Group Should be disabled : ");
        assertTrue(loadGroup.isDisableControl() == (loadGroupResponse.isDisableControl()), "Control Should be disabled : ");

        Log.endTestCase("loadGroupPoint_02_Get");
    }

    @Test(dependsOnMethods = "loadGroupPoint_02_Get")
    public void loadGroupPoint_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupPoint_03_Update");

        MockLoadGroupPoint loadGroup = (MockLoadGroupPoint) context.getAttribute("expectedloadGroup");
        String name = "LM_Group_Point_Name_Update";
        loadGroup.setName(name);
        loadGroup.setKWCapacity(888.0);
        context.setAttribute("Point_GrpName", name);

        Log.info("Updated Load Group is :" + loadGroup);
        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup",
                loadGroup,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        MockLoadGroupPoint updatedLoadGroupResponse = getupdatedResponse.as(MockLoadGroupPoint.class);
        assertTrue(name.equals(updatedLoadGroupResponse.getName()), "Name Should be : " + name);
        assertTrue(loadGroup.getType().equals(updatedLoadGroupResponse.getType()), "Type Should be : " + loadGroup.getType());
        assertTrue(loadGroup.getKWCapacity().equals(updatedLoadGroupResponse.getKWCapacity()),
                "kWCapacity Should be : " + loadGroup.getKWCapacity());
        Log.endTestCase("loadGroupPoint_03_Update");
    }

    @Test(dependsOnMethods = "loadGroupPoint_03_Update")
    public void loadGroupPoint_04_Copy(ITestContext context) {

        Log.startTestCase("loadGroupPoint_04_Copy");
        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_POINT)).build();
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(copyResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null, "Group Id should not be Null");
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupPoint loadGroupResponse = getResponse.as(MockLoadGroupPoint.class);
        context.setAttribute("Copied_Point_GrpName", loadGroupResponse.getName());
        context.setAttribute("Copied_Point_GrpId", loadGroupResponse.getId());
        Log.endTestCase("loadGroupPoint_04_Copy");
    }

    @Test(dependsOnMethods = "loadGroupPoint_04_Copy")
    public void loadGroupPoint_05_Delete(ITestContext context) {
        Log.startTestCase("loadGroupPoint_05_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Point_GrpName").toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(deleteResponse.statusCode() == 200, "Status code should be 200");

        // Delete copy Load group
        lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Copied_Point_GrpName").toString()).build();
        ExtractableResponse<?> deleteCopyResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute("Copied_Point_GrpId").toString());
        assertTrue(deleteCopyResponse.statusCode() == 200, "Status code should be 200");

        Log.endTestCase("loadGroupPoint_05_Delete");
    }

    /**
     * Negative validation when same Group name is passed that is used while creation of Point load group created via
     * loadGroupPoint_01_Create
     */
    @Test(dependsOnMethods = "loadGroupPoint_01_Create")
    public void loadGroupPoint_06_GroupNameIsSameValidation(ITestContext context) {
        Log.startTestCase("loadGroupPoint_06_GroupNameIsSameValidation");

        MockLoadGroupPoint loadGroup = (MockLoadGroupPoint) context.getAttribute("expectedloadGroup");
        loadGroup.setName(loadGroup.getName());
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name must be unique."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_06_GroupNameIsSameValidation");

    }

    /**
     * Negative validation when Group name field is passed as blank while creation of Point load group
     */
    @Test
    public void loadGroupPoint_07_GroupNameAsBlankValidation(ITestContext context) {

        Log.startTestCase("loadGroupPoint_07_GroupNameAsBlankValidation");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        loadGroup.setName("");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_07_GroupNameAsBlankValidation");
    }

    /**
     * Negative validation when Group name is passed with special characters while creation of Point load group
     */
    @Test
    public void loadGroupPoint_08_GroupNameWithSpecialCharsValidation(ITestContext context) {

        Log.startTestCase("loadGroupPoint_08_GroupNameWithSpecialCharsValidation");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        loadGroup.setName("Test\\,Point");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name",
                "Cannot be blank or include any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_08_GroupNameWithSpecialCharsValidation");
    }

    /**
     * Negative validation when Group name is passed with more than 60 characters while creation of Point load group
     */
    @Test
    public void loadGroupPoint_09_GroupNameWithMoreThanSixtyCharsValidation(ITestContext context) {

        Log.startTestCase("loadGroupPoint_09_GroupNameWithMoreThanSixtyCharsValidation");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        loadGroup.setName("TestPointMoreThanSixtyCharacter_TestPointMoreThanSixtyCharacters");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Exceeds maximum length of 60."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_09_GroupNameWithMoreThanSixtyCharsValidation");
    }

    /**
     * Negative validation when Kw Capacity field is passed as blank while creation of Point load group
     */
    @Test
    public void loadGroupPoint_10_KwCapacityAsBlankValidation() {

        Log.startTestCase("loadGroupPoint_10_KwCapacityAsBlankValidation");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        loadGroup.setKWCapacity(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");

        assertTrue(ValidationHelper.validateFieldError(createResponse, "kWCapacity", "kW Capacity is required."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_10_KwCapacityAsBlankValidation");
    }

    /**
     * Negative validation when Kw Capacity field value is less than the minimum value allowed while creation of
     * Point load group
     */
    @Test
    public void loadGroupPoint_11_KwCapacityIsLessThanMinValueValidation() {

        Log.startTestCase("loadGroupPoint_11_KwCapacityIsLessThanMinValueValidation");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        loadGroup.setKWCapacity(-1.000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "kWCapacity", "Must be between 0 and 99,999.999."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_11_KwCapacityIsLessThanMinValueValidation");
    }

    /**
     * Negative validation when Kw Capacity field value is greater than the Maximum value allowed while creation of
     * Point load group
     */
    @Test
    public void loadGroupPoint_12_KwCapacityIsGreaterThanMaxValueValidation() {

        Log.startTestCase("loadGroupPoint_12_KwCapacityIsgreaterThanMaxValueValidation");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        loadGroup.setKWCapacity(100000.0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "kWCapacity", "Must be between 0 and 99,999.999."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_12_KwCapacityIsgreaterThanMaxValueValidation");
    }

    /**
     * Negative validation when Control device is other than RTU, MCT, CBC, ION
     * Point load group
     */
    @Test
    public void loadGroupPoint_13_CntDeviceIsOtherThanRTU_MCT_CBC_ION() {

        Log.startTestCase("loadGroupPoint_13_CntDeviceIsOtherThanRTU_MCT_CBC_ION");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        MockLMDto deviceUsage = MockLMDto.builder().id(29467).build();
        loadGroup.setDeviceUsage(deviceUsage);
        MockLMDto pointUsage = MockLMDto.builder().id(176266).build();
        loadGroup.setPointUsage(pointUsage);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "deviceUsage.id",
                        "Invalid device type, valid types include MCT, RTU, CBC, or ION."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_13_CntDeviceIsOtherThanRTU_MCT_CBC_ION");
    }

    /**
     * Negative validation when Control device is Blank
     * Point load group
     */
    @Test
    public void loadGroupPoint_14_CntDeviceIsBlank() {

        Log.startTestCase("loadGroupPoint_14_CntDeviceIsBlank");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        MockLMDto deviceUsage = MockLMDto.builder().id(null).build();
        loadGroup.setDeviceUsage(deviceUsage);
        MockLMDto pointUsage = MockLMDto.builder().id(176266).build();
        loadGroup.setPointUsage(pointUsage);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "deviceUsage.id", "Control Device Point is required."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_14_CntDeviceIsBlank");
    }

    /**
     * Negative validation when Control device point is Blank
     * Point load group
     */
    @Test
    public void loadGroupPoint_15_CntDevicePointIsBlank() {

        Log.startTestCase("loadGroupPoint_15_CntDevicePointIsBlank");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        MockLMDto deviceUsage = MockLMDto.builder().id(11735).build();
        loadGroup.setDeviceUsage(deviceUsage);
        MockLMDto pointUsage = MockLMDto.builder().id(null).build();
        loadGroup.setPointUsage(pointUsage);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "pointUsage.id", "Control Device Point is required."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_15_CntDevicePointIsBlank");
    }

    /**
     * Negative validation when Control Start State is Blank
     * Point load group
     */
    @Test
    public void loadGroupPoint_16_CntStartStateIsBlank() {

        Log.startTestCase("loadGroupPoint_16_CntStartStateIsBlank");

        MockLoadGroupPoint loadGroup = buildMockLoadGroup();
        loadGroup.setStartControlRawState(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "startControlRawState", "Control Start State is required."),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupPoint_16_CntStartStateIsBlank");
    }

    /**
     * This function build Mock Load Group payload to be used for negative scenarios test cases
     */
    public MockLoadGroupPoint buildMockLoadGroup() {

        MockLoadGroupPoint loadGroup = (MockLoadGroupPoint) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_POINT);

        return loadGroup;
    }

}
