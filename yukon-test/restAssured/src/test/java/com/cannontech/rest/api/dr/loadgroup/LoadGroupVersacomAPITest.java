package com.cannontech.rest.api.dr.loadgroup;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockApiFieldError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupVersacom;
import com.cannontech.rest.api.loadgroup.request.MockVersacomAddressUsage;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class LoadGroupVersacomAPITest {

    /**
     * This test case validates creation of Versacom load group with default values
     */
    @Test
    public void loadGroupVersacom_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupVersacom_01_Create");
        MockLoadGroupVersacom loadGroup = (MockLoadGroupVersacom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_VERSACOM);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID));
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID) != null, "Group Id should not be Null");
        loadGroup.setId(createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID));
        context.setAttribute("expectedloadGroup", loadGroup);
        Log.endTestCase("loadGroupVersacom_01_Create");
    }

    /**
     * This test case validates fields of Versacom load group created via loadGroupVersacom_01_Create
     */
    @Test(dependsOnMethods = "loadGroupVersacom_01_Create")
    public void loadGroupVersacom_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupVersacom_02_Get");
        Log.info("Group Id of LmGroupVersacom created is : " + context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID));

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupVersacom loadGroupResponse = getResponse.as(MockLoadGroupVersacom.class);
        context.setAttribute("Versacom_GrpName", loadGroupResponse.getName());

        MockLoadGroupVersacom loadGroup = (MockLoadGroupVersacom) context.getAttribute("expectedloadGroup");

        assertTrue(loadGroup.getName().equals(loadGroupResponse.getName()), "Name Should be : " + loadGroup.getName());
        assertTrue(loadGroup.getType() == loadGroupResponse.getType(), "Type Should be : " + loadGroup.getType());
        assertTrue(loadGroup.getKWCapacity().equals(loadGroupResponse.getKWCapacity()),
                "kWCapacity Should be : " + loadGroup.getKWCapacity());
        assertTrue(loadGroup.isDisableGroup() == (loadGroupResponse.isDisableGroup()), "Group Should be disabled : ");
        assertTrue(loadGroup.isDisableControl() == (loadGroupResponse.isDisableControl()), "Control Should be disabled : ");

        Log.endTestCase("loadGroupVersacom_02_Get");
    }

    /**
     * This test case updates name of Versacom load group created via loadGroupVersacom_01_Create
     */
    @Test(dependsOnMethods = "loadGroupVersacom_02_Get")
    public void loadGroupVersacom_03_Update(ITestContext context) {
        Log.startTestCase("loadGroupVersacom_03_Update");

        MockLoadGroupVersacom loadGroup = (MockLoadGroupVersacom) context.getAttribute("expectedloadGroup");
        String name = "LM_Group_Versacom_Name_Update";
        loadGroup.setName(name);
        loadGroup.setKWCapacity(888.0);
        context.setAttribute("Versacom_GrpName", name);

        Log.info("Updated Load Group is :" + loadGroup);
        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup",
                loadGroup,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        MockLoadGroupVersacom updatedLoadGroupResponse = getupdatedResponse.as(MockLoadGroupVersacom.class);
        assertTrue(name.equals(updatedLoadGroupResponse.getName()), "Name Should be : " + name);
        assertTrue(loadGroup.getType().equals(updatedLoadGroupResponse.getType()), "Type Should be : " + loadGroup.getType());
        assertTrue(loadGroup.getKWCapacity().equals(updatedLoadGroupResponse.getKWCapacity()),
                "kWCapacity Should be : " + loadGroup.getKWCapacity());
        Log.endTestCase("loadGroupVersacom_03_Update");
    }

    /**
     * This test case copies Versacom load group created via loadGroupVersacom_01_Create
     */
    @Test(dependsOnMethods = "loadGroupVersacom_03_Update")
    public void loadGroupVersacom_04_Copy(ITestContext context) {

        Log.startTestCase("loadGroupVersacom_04_Copy");

        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_VERSACOM)).build();
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(copyResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null, "Group Id should not be Null");
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupVersacom loadGroupResponse = getResponse.as(MockLoadGroupVersacom.class);
        context.setAttribute("Copied_Versacom_GrpName", loadGroupResponse.getName());
        context.setAttribute("Copied_Versacom_GrpId", loadGroupResponse.getId());

        Log.endTestCase("loadGroupVersacom_04_Copy");
    }

    /**
     * Negative validation when same Group name is passed that is used while creation of Versacom load group created via
     * loadGroupVersacom_01_Create
     */
    @Test(dependsOnMethods = "loadGroupVersacom_01_Create")
    public void loadGroupVersacom_05_GroupName_Is_Same_Validation(ITestContext context) {

        Log.startTestCase("loadGroupVersacom_05_GroupName_Is_Same_Validation");

        MockLoadGroupVersacom loadGroup = (MockLoadGroupVersacom) context.getAttribute("expectedloadGroup");
        loadGroup.setName(loadGroup.getName());
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);

        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "name");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue((validationCode.get("message")).equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Name must be unique.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_05_GroupName_Is_Same_Validation");
    }

    /**
     * Negative validation when Group name field is passed as blank while creation of Versacom load group
     */
    @Test
    public void loadGroupVersacom_06_GroupName_As_Blank_Validation(ITestContext context) {

        Log.startTestCase("loadGroupVersacom_06_GroupName_As_Blank_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setName("");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "name");
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Name is required.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_06_GroupName_As_Blank_Validation");
    }

    /**
     * Negative validation when Group name is passed with special characters while creation of Versacom load group
     */
    @Test
    public void loadGroupVersacom_07_GroupName_With_Special_Characters_Validation(ITestContext context) {

        Log.startTestCase("loadGroupVersacom_07_GroupName_With_Special_Characters_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setName("Test\\,Versacom");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "name");
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Cannot be blank or include any of the following characters: / \\ , ' \" |".equals(validationCode.get("code")),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_07_GroupName_With_Special_Characters_Validation");
    }

    /**
     * Negative validation when Group name is passed with more than 60 characters while creation of Versacom load group
     */
    @Test
    public void loadGroupVersacom_08_GroupName_With_MoreThan_Sixty_Characters_Validation(ITestContext context) {

        Log.startTestCase("loadGroupVersacom_08_GroupName_With_MoreThan_Sixty_Characters_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setName("TestVersacomMoreThanSixtyCharacter_TestVersacomMoreThanSixtyCharacters");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "name");
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Exceeds maximum length of 60.".equals(validationCode.get("code")),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_08_GroupName_With_MoreThan_Sixty_Characters_Validation");
    }

    /**
     * Negative validation when Kw Capacity field is passed as blank while creation of Versacom load group
     */
    @Test
    public void loadGroupVersacom_09_KwCapacity_As_Blank_Validation() {

        Log.startTestCase("loadGroupVersacom_09_KwCapacity_As_Blank_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setKWCapacity(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "kWCapacity");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("kW Capacity is required.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_09_KwCapacity_As_Blank_Validation");
    }

    /**
     * Negative validation when Kw Capacity field value is less than the minimum value allowed while creation of
     * Versacom load group
     */
    @Test
    public void loadGroupVersacom_10_KwCapacity_Is_Less_Than_MinValue_Validation() {

        Log.startTestCase("loadGroupVersacom_10_KwCapacity_Is_Less_Than_MinValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setKWCapacity(-222.0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "kWCapacity");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 0 and 99,999.999.".equals(validationCode.get("code")),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_10_KwCapacity_Is_Less_Than_MinValue_Validation");
    }

    /**
     * Negative validation when Kw Capacity field value is greater than the Maximum value allowed while creation of
     * Versacom load group
     */
    @Test
    public void loadGroupVersacom_11_KwCapacity_Is_greater_Than_MaxValue_Validation() {

        Log.startTestCase("loadGroupVersacom_11_KwCapacity_Is_greater_Than_MaxValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setKWCapacity(100000.0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "kWCapacity");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 0 and 99,999.999.".equals(validationCode.get("code")),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_11_KwCapacity_Is_greater_Than_MaxValue_Validation");
    }

    /**
     * Negative validation when Utility Address field is passed as blank while creation of Versacom load group
     */
    @Test
    public void loadGroupVersacom_12_UtilityAddress_As_Blank_Validation() {

        Log.startTestCase("loadGroupVersacom_12_UtilityAddress_As_Blank_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setUtilityAddress(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "utilityAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Utility Address is required.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_12_UtilityAddress_As_Blank_Validation");
    }

    /**
     * Negative validation when Utility Address field value is more than max value allowed while creation of Versacom
     * load group
     */
    @Test
    public void loadGroupVersacom_13_UtilityAddress_MaxValue_Validation() {

        Log.startTestCase("loadGroupVersacom_13_UtilityAddress_MaxValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setUtilityAddress(257);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "utilityAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 1 and 254.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_13_UtilityAddress_MaxValue_Validation");
    }

    /**
     * Negative validation when Utility Address field value is less than min value allowed while creation of Versacom
     * load group
     */
    @Test
    public void loadGroupVersacom_14_UtilityAddress_MinValue_Validation() {

        Log.startTestCase("loadGroupVersacom_14_UtilityAddress_MinValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setUtilityAddress(257);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "utilityAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 1 and 254.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_14_UtilityAddress_MinValue_Validation");
    }

    /**
     * Negative validation when Section Address field is passed as blank while creation of Versacom load group
     */
    @Test
    public void loadGroupVersacom_15_SectionAddress_As_Blank_Validation() {

        Log.startTestCase("loadGroupVersacom_15_SectionAddress_As_Blank_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setSectionAddress(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "sectionAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Section Address is required.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_15_SectionAddress_As_Blank_Validation");
    }

    /**
     * Negative validation when Section Address field value is greater than the maximum value allowed while creation of
     * Versacom load group
     */
    @Test
    public void loadGroupVersacom_16_SectionAddress_MaxValue_Validation() {

        Log.startTestCase("loadGroupVersacom_16_SectionAddress_MaxValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setSectionAddress(257);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "sectionAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 0 and 256.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_16_SectionAddress_MaxValue_Validation");
    }

    /**
     * Negative validation when Section Address field value is less than the minimum value allowed while creation of
     * Versacom load group
     */
    @Test
    public void loadGroupVersacom_17_SectionAddress_MinValue_Validation() {

        Log.startTestCase("loadGroupVersacom_17_SectionAddress_MinValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setSectionAddress(-3);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "sectionAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 0 and 256.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_17_SectionAddress_MinValue_Validation");
    }

    /**
     * Negative validation when Class Address field is passed as text while creation of Versacom load group
     */
    @Test
    public void loadGroupVersacom_18_ClassAddress_AsText_Validation(ITestContext context) {
        Log.startTestCase("loadGroupVersacom_18_ClassAddress_AsText_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setName("TestVersacom1");
        loadGroup.setClassAddress("Text");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 200, "Status code should be " + 200);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupVersacom loadGroupGetResponse = getResponse.as(MockLoadGroupVersacom.class);
        assertTrue(loadGroupGetResponse.getClassAddress().equals("0000000000000000"),
                "Class Address should be " + "0000000000000000");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(loadGroupGetResponse.getName()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteloadgroup", lmDeleteObject,
                loadGroupGetResponse.getId().toString());
        assertTrue(deleteResponse.statusCode() == 200, "Status code should be 200");

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedLoadGroupResponse = ApiCallHelper.get("getloadgroup",
                loadGroupGetResponse.getId().toString());
        assertTrue(getDeletedLoadGroupResponse.statusCode() == 400, "Status code should be 400");
        MockApiError error = getDeletedLoadGroupResponse.as(MockApiError.class);
        assertTrue("Id not found".equals(error.getMessage()), "Expected error message Should be : " + "Id not found");

        Log.endTestCase("loadGroupVersacom_18_ClassAddress_AsText_Validation");

    }

    /**
     * Negative validation when Class Address field value greater than the Maximum value allowed while creation of
     * Versacom load group
     */
    @Test
    public void loadGroupVersacom_19_ClassAddressValue_Greater_MaxValue_Validation() {

        Log.startTestCase("loadGroupVersacom_20_ClassAddressValue_Greater_MaxValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setClassAddress("10000000000100011");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "classAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 0 and 65,535.".equals(validationCode.get("code")),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_20_ClassAddressValue_Greater_MaxValue_Validation");
    }

    /**
     * Negative validation when Division Address field is passed as text while creation of Versacom load group
     */
    @Test
    public void loadGroupVersacom_20_DivisionAddress_AsText_Validation(ITestContext context) {
        Log.startTestCase("loadGroupVersacom_20_DivisionAddress_AsText_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setName("TestVersacom1");
        loadGroup.setDivisionAddress("Text");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 200, "Status code should be " + 200);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockLoadGroupVersacom loadGroupGetResponse = getResponse.as(MockLoadGroupVersacom.class);
        assertTrue(loadGroupGetResponse.getDivisionAddress().equals("0000000000000000"),
                "division Address should be " + "0000000000000000");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(loadGroupGetResponse.getName()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteloadgroup", lmDeleteObject,
                loadGroupGetResponse.getId().toString());
        assertTrue(deleteResponse.statusCode() == 200, "Status code should be 200");

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedLoadGroupResponse = ApiCallHelper.get("getloadgroup",
                loadGroupGetResponse.getId().toString());
        assertTrue(getDeletedLoadGroupResponse.statusCode() == 400, "Status code should be 400");
        MockApiError error = getDeletedLoadGroupResponse.as(MockApiError.class);
        assertTrue("Id not found".equals(error.getMessage()), "Expected error message Should be : " + "Id not found");

        Log.endTestCase("loadGroupVersacom_20_DivisionAddress_AsText_Validation");

    }

    /**
     * Negative validation when Division Address field value greater than the Maximum value allowed while creation of
     * Versacom load group
     */
    @Test
    public void loadGroupVersacom_21_DivisionAddressValue_Greater_MaxValue_Validation() {

        Log.startTestCase("loadGroupVersacom_21_DivisionAddressValue_Greater_MaxValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        loadGroup.setDivisionAddress("10000000000100011");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "divisionAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 0 and 65,535.".equals(validationCode.get("code")),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_21_DivisionAddressValue_Greater_MaxValue_Validation");
    }

    /**
     * Negative validation when Serial Address field value is greater than the maximum value allowed while creation of
     * Versacom load group
     */
    @Test
    public void loadGroupVersacom_22_SerialAddress_MaxValue_Validation() {
        Log.startTestCase("loadGroupVersacom_22_SerialAddress_MaxValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        add_Serial_In_AddressUsage(loadGroup);
        loadGroup.setSerialAddress("1000000");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "serialAddress");

        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 1 and 99,999.".equals(validationCode.get("code")),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_22_SerialAddress_MaxValue_Validation");
    }

    /**
     * Negative validation when Serial Address field value is less than the minimum value allowed while creation of
     * Versacom load group
     */
    @Test
    public void loadGroupVersacom_23_SerialAddress_MinValue_Validation() {

        Log.startTestCase("loadGroupVersacom_23_SerialAddress_MinValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        add_Serial_In_AddressUsage(loadGroup);
        loadGroup.setSerialAddress("-3");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "serialAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Must be between 1 and 99,999.".equals(validationCode.get("code")),
                "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_23_SerialAddress_MinValue_Validation");
    }

    /**
     * Negative validation when Serial Address field is passed as String while creation of Versacom load group
     */
    @Test
    public void loadGroupVersacom_24_SerialAddress_StringValue_Validation() {

        Log.startTestCase("loadGroupVersacom_24_SerialAddress_StringValue_Validation");

        MockLoadGroupVersacom loadGroup = buildMockLoadGroup();
        add_Serial_In_AddressUsage(loadGroup);
        loadGroup.setSerialAddress("Test");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        HashMap<String, String> validationCode = fetchValidationCodeForFieldError(createResponse, "serialAddress");
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(validationCode.get("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue("Invalid value.".equals(validationCode.get("code")), "Expected code in response is not correct");

        Log.endTestCase("loadGroupVersacom_24_SerialAddress_StringValue_Validation");
    }

    /**
     * This test case deletes Versacom load group created via loadGroupVersacom_01_Create
     */
    @Test(dependsOnMethods = "loadGroupVersacom_04_Copy")
    public void loadGroupVersacom_25_Delete(ITestContext context) {
        Log.startTestCase("loadGroupVersacom_25_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("Versacom_GrpName").toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(deleteResponse.statusCode() == 200, "Status code should be 200");

        MockLMDto lmDeleteCopyObject = MockLMDto.builder().name(context.getAttribute("Copied_Versacom_GrpName").toString())
                .build();

        Log.info("Delete Load Group is : " + lmDeleteCopyObject);
        ExtractableResponse<?> deleteCopyResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteCopyObject,
                context.getAttribute("Copied_Versacom_GrpId").toString());
        assertTrue(deleteCopyResponse.statusCode() == 200, "Status code should be 200");

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedLoadGroupResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue(getDeletedLoadGroupResponse.statusCode() == 400, "Status code should be 400");
        MockApiError error = getDeletedLoadGroupResponse.as(MockApiError.class);
        assertTrue("Id not found".equals(error.getMessage()), "Expected error message Should be : " + "Id not found");

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedCopiedLoadGroupResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute("Copied_Versacom_GrpId").toString());
        assertTrue(getDeletedCopiedLoadGroupResponse.statusCode() == 400, "Status code should be 400");
        MockApiError errorCopy = getDeletedCopiedLoadGroupResponse.as(MockApiError.class);
        assertTrue("Id not found".equals(errorCopy.getMessage()), "Expected error message Should be : " + "Id not found");

        Log.startTestCase("loadGroupVersacom_25_Delete");
    }

	/**
	 * Negative validation when Load Group is copied with invalid Route Id
	 */
	@Test(dependsOnMethods = "loadGroupVersacom_01_Create")
	public void loadGroupVersacom_26_CopyWithInvalidRouteId(ITestContext context) {

		MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
				.name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_VERSACOM)).build();
		loadGroupCopy.setRouteId(2222222);
		ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup", loadGroupCopy,
				context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
		assertTrue(copyResponse.statusCode() == 422, "Status code should be " + 422);
		assertTrue(ValidationHelper.validateErrorMessage(copyResponse, "Validation error"),
				"Expected message should be - Validation error");
		assertTrue(ValidationHelper.validateFieldError(copyResponse, "routeId", "Route Id does not exist."),
				"Expected code in response is not correct");
	}

    /**
     * This function build Mock Load Group payload to be used for negative scenarios test cases
     */
    public MockLoadGroupVersacom buildMockLoadGroup() {

        MockLoadGroupVersacom loadGroup = (MockLoadGroupVersacom) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_VERSACOM);

        return loadGroup;
    }

    /**
     * This function add SERIAL as Address Usage while creation of Versacom load group
     */
    public void add_Serial_In_AddressUsage(MockLoadGroupVersacom loadGroup) {

        List<MockVersacomAddressUsage> versacomSerialAddressUsage = new ArrayList<>();
        versacomSerialAddressUsage = loadGroup.getAddressUsage();
        versacomSerialAddressUsage.add(MockVersacomAddressUsage.SERIAL);
        loadGroup.setAddressUsage(versacomSerialAddressUsage);

    }

    /**
     * This function fetches Validation Code For the Field passed as argument while creation of Versacom load group
     */
    public HashMap<String, String> fetchValidationCodeForFieldError(ExtractableResponse<?> response, String field) {
        HashMap<String, String> validationCode = new HashMap<String, String>();
        MockApiError error = response.as(MockApiError.class);
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = "";
        String message = error.getMessage();
        for (MockApiFieldError fieldError : fieldErrors) {
            if (fieldError.getField().equals(field)) {
                code = fieldError.getCode();
                break;
            }
        }
        validationCode.put("message", message);
        validationCode.put("code", code);

        return validationCode;
    }
}