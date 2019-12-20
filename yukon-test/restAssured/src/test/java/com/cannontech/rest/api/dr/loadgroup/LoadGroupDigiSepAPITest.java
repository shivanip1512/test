package com.cannontech.rest.api.dr.loadgroup;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockApiFieldError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupDigiSep;
import com.cannontech.rest.api.loadgroup.request.MockSepDeviceClass;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class LoadGroupDigiSepAPITest {

    private MockLoadGroupDigiSep loadGroup = null;
    private HashMap<Integer, String> groups = new HashMap<Integer, String>();

    @BeforeMethod
    public void setUp() {
        loadGroup = (MockLoadGroupDigiSep) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        List<MockSepDeviceClass> deviceClassSet = new ArrayList<>();
        deviceClassSet.add(MockSepDeviceClass.BASEBOARD_HEAT);
        deviceClassSet.add(MockSepDeviceClass.ELECTRIC_VEHICLE);
        deviceClassSet.add(MockSepDeviceClass.IRRIGATION_PUMP);
        loadGroup.setDeviceClassSet(deviceClassSet);
    }

    /**
     * This test case validates creation of DigiSep load group with default values provided in payload json file
     */
    @Test
    public void loadGroupDigiSep_01_Create(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_01_Create");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Group Id should not be Null", createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);

        Log.endTestCase("loadGroupDigiSep_01_Create");
    }

    /**
     * This test case validates retrieval(Get) of DigiSep load group and validates response
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_02_Get(ITestContext context) {
        Log.startTestCase("loadGroupNest_02_Get");

        Log.info("GroupId of LmGroupDigiSep created is : " + context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupBase digiSepLoadGroupBase = getResponse.as(MockLoadGroupBase.class);
        context.setAttribute("DigiSep_GrpName", digiSepLoadGroupBase.getName());

        assertTrue("Name should be : " + loadGroup.getName(), loadGroup.getName().equals(digiSepLoadGroupBase.getName()));
        assertTrue("Type should be : " + loadGroup.getType(), loadGroup.getType() == digiSepLoadGroupBase.getType());

        assertTrue("Group should be disabled : ", !digiSepLoadGroupBase.isDisableGroup());
        assertTrue("Control should be enabled : ", digiSepLoadGroupBase.isDisableControl());

        MockLoadGroupDigiSep digiSepLoadGroup = getResponse.as(MockLoadGroupDigiSep.class);
        assertTrue("UtilityEnrollmentGroup should be : " + loadGroup.getUtilityEnrollmentGroup(),
                loadGroup.getUtilityEnrollmentGroup().equals(digiSepLoadGroup.getUtilityEnrollmentGroup()));

        assertTrue("DeviceClassSet should be : " + (loadGroup.getUtilityEnrollmentGroup().toString()),
                loadGroup.getUtilityEnrollmentGroup().equals(digiSepLoadGroup.getUtilityEnrollmentGroup()));

        assertTrue("RampInMinutes should be : " + loadGroup.getRampInMinutes(),
                loadGroup.getRampInMinutes().equals(digiSepLoadGroup.getRampInMinutes()));
        assertTrue("RampOutMinutes should be : " + loadGroup.getRampOutMinutes(),
                loadGroup.getRampOutMinutes().equals(digiSepLoadGroup.getRampOutMinutes()));

        assertTrue("kWCapacity should be : " + loadGroup.getKWCapacity(),
                loadGroup.getKWCapacity().equals(digiSepLoadGroup.getKWCapacity()));

        Log.endTestCase("loadGroupDigiSep_01_Create");

    }

    /**
     * This test case validates update of DigiSep load group and validates response with updated values
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_03_Update(ITestContext context) {

        Log.startTestCase("loadGroupDigiSep_03_Update");

        String groupName = "DigiSepUpdate";
        context.setAttribute("DigiSep_GrpName", groupName);

        loadGroup = (MockLoadGroupDigiSep) LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        loadGroup.setName(groupName);
        loadGroup.setDisableGroup(true);
        loadGroup.setDisableControl(false);
        loadGroup.setUtilityEnrollmentGroup(9);

        List<MockSepDeviceClass> deviceClassSetUpdate = new ArrayList<>();
        deviceClassSetUpdate.add(MockSepDeviceClass.EXTERIOR_LIGHTING);
        deviceClassSetUpdate.add(MockSepDeviceClass.GENERATION_SYSTEMS);
        deviceClassSetUpdate.add(MockSepDeviceClass.POOL_PUMP);
        loadGroup.setDeviceClassSet(deviceClassSetUpdate);

        loadGroup.setRampInMinutes(28);
        loadGroup.setRampOutMinutes(28);
        loadGroup.setKWCapacity(19.28);

        Log.info("Updated Load Group is :" + loadGroup);

        ExtractableResponse<?> getResponse = ApiCallHelper.post("updateloadgroup", loadGroup,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        MockLoadGroupBase updatedDigiSepLoadGroup = getupdatedResponse.as(MockLoadGroupBase.class);
        assertTrue("Name Should be : " + groupName, groupName.equals(updatedDigiSepLoadGroup.getName()));
        assertTrue("Type Should be : " + loadGroup.getType(), loadGroup.getType() == updatedDigiSepLoadGroup.getType());
        assertTrue("kWCapacity Should be : " + loadGroup.getKWCapacity(),
                loadGroup.getKWCapacity().equals(updatedDigiSepLoadGroup.getKWCapacity()));

        Log.endTestCase("loadGroupDigiSep_03_Update");
    }

    /**
     * This test case validates copy of DigiSep load group and validates response with copied values
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_04_Copy(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_04_Copy");

        MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
                .name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_DIGI_SEP)).build();

        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyloadgroup",
                loadGroupCopy,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());

        assertTrue("Group Id should not be Null", copyResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);

        // To tear down after all scripts
        groups.put(copyResponse.path("groupId"), loadGroupCopy.getName());

        Log.endTestCase("loadGroupDigiSep_04_Copy");
    }

    /**
     * This test case validates deletion of DigiSep load group
     */
    @Test(dependsOnMethods = { "loadGroupDigiSep_01_Create" })
    public void loadGroupDigiSep_05_Delete(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_05_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("DigiSep_GrpName").toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Validate load group is deleted
        ExtractableResponse<?> getDeletedResponse = ApiCallHelper.get("getloadgroup",
                context.getAttribute(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 400", getDeletedResponse.statusCode() == 400);

        MockApiError error = getDeletedResponse.as(MockApiError.class);
        assertTrue("Expected error message Should be : " + "Id not found", "Id not found".equals(error.getMessage()));

        Log.endTestCase("loadGroupDigiSep_05_Delete");
    }

    /**
     * This test case validates creation of DigiSep load group with Empty Group name
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_06_CreateWithEmptyName() {
        Log.startTestCase("loadGroupDigiSep_06_CreateWithEmptyName");

        loadGroup.setName("");

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", response.statusCode() == 422);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Name is required."));

        Log.endTestCase("loadGroupDigiSep_06_CreateWithEmptyName");
    }

    /**
     * This test case validates creation of DigiSep load group with existing Group name
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_07_CreateWithExistingName() {
        Log.startTestCase("loadGroupDigiSep_07_CreateWithExistingName");

        loadGroup.setName("DigiSepTestCopy");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        MockApiError error = createResponse.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Name must be unique."));

        Log.endTestCase("loadGroupDigiSep_07_CreateWithExistingName");
    }

    /**
     * This test case validates creation of DigiSep load group with not allowed chars in Group name Characters not
     * allowed for Group Name "/", "\", "," (both slash and comma)
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_08_CreateWithSpecialCharsInName() {
        Log.startTestCase("loadGroupDigiSep_08_CreateWithSpecialCharsInName");

        ArrayList<String> groupNameList = new ArrayList<String>();
        groupNameList.add("DigiSepUpdate\\Test");
        groupNameList.add("DigiSepUpdate//Test");
        groupNameList.add("DigiSepUpdate,Test");

        for (int i = 0; i < groupNameList.size(); i++) {
            loadGroup.setName(groupNameList.get(i));

            ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
            assertTrue("Status code should be 422", response.statusCode() == 422);
            MockApiError error = response.as(MockApiError.class);
            assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
            List<MockApiFieldError> fieldErrors = error.getFieldErrors();
            String code = fieldErrors.get(0).getCode();
            assertTrue("Expected code in response is not correct",
                    code.equals("Cannot be blank or include any of the following characters: / \\ , ' \" |"));
        }

        Log.endTestCase("loadGroupDigiSep_08_CreateWithSpecialCharsInName");
    }

    /**
     * This test case validates creation of DigiSep load group with above maximum length for Group name Maximum length
     * for Group Name is 60
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_09_CreateGroupWithMaxCharLengthInName() {
        Log.startTestCase("loadGroupDigiSep_09_CreateGroupWithMaxCharLengthInName");

        loadGroup.setName("TestDigiSepMoreThanSixtyCharacter_TestDigiSepMoreThanSixtyCharacters");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        MockApiError error = createResponse.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Exceeds maximum length of 60."));

        Log.endTestCase("loadGroupDigiSep_09_CreateGroupWithMaxCharLengthInName");
    }

    /**
     * This test case validates creation of DigiSep load group with empty KwCapacity
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_10_CreateGroupWithEmptyKwCapacity() {
        Log.startTestCase("loadGroupDigiSep_10_CreateGroupWithEmptyKwCapacity");

        loadGroup.setKWCapacity(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        MockApiError error = createResponse.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("kW Capacity is required."));

        Log.endTestCase("loadGroupDigiSep_10_CreateGroupWithEmptyKwCapacity");
    }

    /**
     * This test case validates creation of DigiSep load group with negative KwCapacity
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_11_CreateGroupWithNegativeKwCapacity() {
        Log.startTestCase("loadGroupDigiSep_11_CreateGroupWithNegativeKwCapacity");

        loadGroup.setKWCapacity((double) -21);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", response.statusCode() == 422);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Must be between 0 and 99,999.999."));

        Log.endTestCase("loadGroupDigiSep_11_CreateGroupWithNegativeKwCapacity");
    }

    /**
     * This test case validates creation of DigiSep load group with maximum value for KwCapacity
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_12_CreateGroupWithMaxKwCapacity(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_12_CreateGroupWithMaxKwCapacity");

        loadGroup.setName("DigiSepTest_KwMaxValue");
        loadGroup.setKWCapacity((double) 99999.999);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupDigiSep digiSepLoadGroup = getResponse.as(MockLoadGroupDigiSep.class);
        assertTrue("kWCapacity should be : " + loadGroup.getKWCapacity(),
                loadGroup.getKWCapacity().equals(digiSepLoadGroup.getKWCapacity()));

        // To tear down after all scripts
        groups.put(createResponse.path("groupId"), digiSepLoadGroup.getName());

        Log.endTestCase("loadGroupDigiSep_12_CreateGroupWithMaxKwCapacity");
    }

    /**
     * This test case validates creation of DigiSep load group with above maximum value for KwCapacity
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_13_CreateGroupWithAboveMaxKwCapacity() {
        Log.startTestCase("loadGroupDigiSep_13_CreateGroupWithAboveMaxKwCapacity");

        loadGroup.setKWCapacity((double) 100000.0);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        MockApiError error = createResponse.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Must be between 0 and 99,999.999."));

        Log.endTestCase("loadGroupDigiSep_13_CreateGroupWithAboveMaxKwCapacity");
    }

    /**
     * This test case validates creation of DigiSep load group with decimal value allowed for KwCapacity If provided
     * KwCapcity is 1234.123456 then it will be converted as 1234.123 If provided KwCapcity is 999.99999 then it will be
     * converted as 1000.0
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_14_CreateGroupWithMoreThanFourDecimalInKwCapacity(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_14_CreateGroupWithMoreThanFourDecimalInKwCapacity");

        ArrayList<String> groupNameList = new ArrayList<String>();
        groupNameList.add("DigiSepDecimalTest_1");
        groupNameList.add("DigiSepDecimalTest_2");

        ArrayList<Double> kwCapacityList = new ArrayList<Double>();
        kwCapacityList.add((double) 1234.123456);
        kwCapacityList.add((double) 999.99999);

        ArrayList<Double> expectedKwCapacityList = new ArrayList<Double>();
        expectedKwCapacityList.add((double) 1234.123);
        expectedKwCapacityList.add((double) 1000.0);

        for (int i = 0; i < kwCapacityList.size(); i++) {
            loadGroup.setName(groupNameList.get(i));
            loadGroup.setKWCapacity(kwCapacityList.get(i));

            ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
            context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID,
                    createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
            assertTrue("Status code should be 200", createResponse.statusCode() == 200);
            assertTrue("Load Group Id should not be Null",
                    createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);

            ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                    createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
            assertTrue("Status code should be 200", getResponse.statusCode() == 200);

            MockLoadGroupDigiSep digiSepLoadGroup = getResponse.as(MockLoadGroupDigiSep.class);
            assertTrue("kWCapacity should be : " + expectedKwCapacityList.get(i),
                    expectedKwCapacityList.get(i).equals(digiSepLoadGroup.getKWCapacity()));

            // To tear down after all scripts
            groups.put(createResponse.path("groupId"), digiSepLoadGroup.getName());
        }

        Log.endTestCase("loadGroupDigiSep_14_CreateGroupWithMoreThanFourDecimalInKwCapacity");
    }

    /**
     * This test case validates creation of DigiSep load group with empty utilityEnrollGroup value
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_15_CreateGroupWithEmptyUtilityEnrollmentGroup() {
        Log.startTestCase("loadGroupDigiSep_15_CreateGroupWithEmptyUtilityEnrollmentGroup");

        loadGroup.setUtilityEnrollmentGroup(null);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", response.statusCode() == 422);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Utility Enrollment Group is required."));

        Log.endTestCase("loadGroupDigiSep_15_CreateGroupWithEmptyUtilityEnrollmentGroup");
    }

    /**
     * This test case validates creation of DigiSep load group with minimum value for utilityEnrollGroup
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_16_CreateGroupWithMinValueInUtilityEnrollmentGroup(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_16_CreateGroupWithMinValueInUtilityEnrollmentGroup");

        loadGroup.setName("DigiSepTest_UtilityEnrollGroupMinValue");
        loadGroup.setUtilityEnrollmentGroup(1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupDigiSep digiSepLoadGroup = getResponse.as(MockLoadGroupDigiSep.class);
        assertTrue("kWCapacity should be : " + loadGroup.getUtilityEnrollmentGroup(),
                loadGroup.getUtilityEnrollmentGroup().equals(digiSepLoadGroup.getUtilityEnrollmentGroup()));

        // To tear down after all scripts
        groups.put(createResponse.path("groupId"), digiSepLoadGroup.getName());

        Log.endTestCase("loadGroupDigiSep_16_CreateGroupWithMinValueInUtilityEnrollmentGroup");
    }

    /**
     * This test case validates creation of DigiSep load group with maximum value for utilityEnrollGroup
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_17_CreateWithMaxValueInUtilityEnrollmentGroup(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_17_CreateWithMaxValueInUtilityEnrollmentGroup");

        loadGroup.setName("DigiSepTest_UtilityEnrollGroupMaxValue");
        loadGroup.setUtilityEnrollmentGroup(255);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupDigiSep digiSepLoadGroup = getResponse.as(MockLoadGroupDigiSep.class);
        assertTrue("kWCapacity should be : " + loadGroup.getUtilityEnrollmentGroup(),
                loadGroup.getUtilityEnrollmentGroup().equals(digiSepLoadGroup.getUtilityEnrollmentGroup()));

        // To tear down after all scripts
        groups.put(createResponse.path("groupId"), digiSepLoadGroup.getName());

        Log.endTestCase("loadGroupDigiSep_17_CreateWithMaxValueInUtilityEnrollmentGroup");
    }

    /**
     * This test case validates creation of DigiSep load group with below minimum value for utilityEnrollGroup
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_18_CreateGroupWithBelowMinValueInUtilityEnrollmentGroup() {
        Log.startTestCase("loadGroupDigiSep_18_CreateGroupWithBelowMinValueInUtilityEnrollmentGroup");

        loadGroup.setUtilityEnrollmentGroup(0);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", response.statusCode() == 422);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Must be between 1 and 255."));

        Log.endTestCase("loadGroupDigiSep_18_CreateGroupWithBelowMinValueInUtilityEnrollmentGroup");
    }

    /**
     * This test case validates creation of DigiSep load group with above maximum value for utilityEnrollGroup
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_19_CreateGroupWithAboveMaxValueInUtilityEnrollmentGroup() {
        Log.startTestCase("loadGroupDigiSep_19_CreateGroupWithAboveMaxValueInUtilityEnrollmentGroup");

        loadGroup.setUtilityEnrollmentGroup(256);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        MockApiError error = createResponse.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Must be between 1 and 255."));

        Log.endTestCase("loadGroupDigiSep_19_CreateGroupWithAboveMaxValueInUtilityEnrollmentGroup");
    }

    /**
     * This test case validates creation of DigiSep load group with negative value for utilityEnrollGroup
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_20_CreateGroupWithNegativeUtilityEnrollmentGroup() {
        Log.startTestCase("loadGroupDigiSep_20_CreateGroupWithNegativeUtilityEnrollmentGroup");

        loadGroup.setUtilityEnrollmentGroup(-21);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", response.statusCode() == 422);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Must be between 1 and 255."));

        Log.endTestCase("loadGroupDigiSep_20_CreateGroupWithNegativeUtilityEnrollmentGroup");
    }

    /**
     * This test case validates creation of DigiSep load group with minimum value for rampInMinutes
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_21_CreateGroupWithMinValueInRampInMinutes(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_21_CreateGroupWithMinValueInRampInMinutes");

        loadGroup.setName("DigiSepTest_RampInMinutesMinValue");
        loadGroup.setRampInMinutes(-99999);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID,
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupDigiSep digiSepLoadGroup = getResponse.as(MockLoadGroupDigiSep.class);
        assertTrue("kWCapacity should be : " + loadGroup.getRampInMinutes(),
                loadGroup.getRampInMinutes().equals(digiSepLoadGroup.getRampInMinutes()));

        // To tear down after all scripts
        groups.put(createResponse.path("groupId"), digiSepLoadGroup.getName());

        Log.endTestCase("loadGroupDigiSep_21_CreateGroupWithMinValueInRampInMinutes");
    }

    /**
     * This test case validates creation of DigiSep load group with maximum value for rampInMinutes
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_22_CreateGroupWithMaxValueInRampInMinutes(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_22_CreateGroupWithMaxValueInRampInMinutes");

        loadGroup.setName("DigiSepTest_RampInMinutesMaxValue");
        loadGroup.setRampInMinutes(99999);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupDigiSep digiSepLoadGroup = getResponse.as(MockLoadGroupDigiSep.class);
        assertTrue("RampInMinutes should be : " + loadGroup.getRampInMinutes(),
                loadGroup.getRampInMinutes().equals(digiSepLoadGroup.getRampInMinutes()));

        // To tear down after all scripts
        groups.put(createResponse.path("groupId"), digiSepLoadGroup.getName());

        Log.endTestCase("loadGroupDigiSep_22_CreateGroupWithMaxValueInRampInMinutes");
    }

    /**
     * This test case validates creation of DigiSep load group with minimum value for rampOutMinutes
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_23_CreateGroupWithMinValueInRampOutMinutes(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_23_CreateGroupWithMinValueInRampOutMinutes");

        loadGroup.setName("DigiSepTest_RampOutMinutesMinValue");
        loadGroup.setRampInMinutes(-99999);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupDigiSep digiSepLoadGroup = getResponse.as(MockLoadGroupDigiSep.class);
        assertTrue("RampOutMinutes should be : " + loadGroup.getRampOutMinutes(),
                loadGroup.getRampOutMinutes().equals(digiSepLoadGroup.getRampOutMinutes()));

        // To tear down after all scripts
        groups.put(createResponse.path("groupId"), digiSepLoadGroup.getName());

        Log.endTestCase("loadGroupDigiSep_23_CreateGroupWithMinValueInRampOutMinutes");
    }

    /**
     * This test case validates creation of DigiSep load group with maximum value for rampOutMinutes
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_24_CreateGroupWithMaxValueInRampOutMinutes(ITestContext context) {
        Log.startTestCase("loadGroupDigiSep_24_CreateGroupWithMaxValueInRampOutMinutes");

        loadGroup.setName("DigiSepTest_RampOutMinutesMaxValue");
        loadGroup.setRampInMinutes(99999);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveloadgroup", loadGroup);
        context.setAttribute(LoadGroupHelper.CONTEXT_GROUP_ID, createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", createResponse.statusCode() == 200);
        assertTrue("Load Group Id should not be Null", createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString() != null);

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getloadgroup",
                createResponse.path(LoadGroupHelper.CONTEXT_GROUP_ID).toString());
        assertTrue("Status code should be 200", getResponse.statusCode() == 200);

        MockLoadGroupDigiSep digiSepLoadGroup = getResponse.as(MockLoadGroupDigiSep.class);
        assertTrue("RampOutMinutes should be : " + loadGroup.getRampOutMinutes(),
                loadGroup.getRampOutMinutes().equals(digiSepLoadGroup.getRampOutMinutes()));

        // To tear down after all scripts
        groups.put(createResponse.path("groupId"), digiSepLoadGroup.getName());

        Log.endTestCase("loadGroupDigiSep_24_CreateGroupWithMaxValueInRampOutMinutes");
    }

    /**
     * This test case validates creation of DigiSep load group with below minimum value for rampInMinutes
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_25_CreateGroupWithBelowMinValueInRampInMinutes() {
        Log.startTestCase("loadGroupDigiSep_25_CreateGroupWithBelowMinValueInRampInMinutes");

        loadGroup.setRampInMinutes(-100000);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", response.statusCode() == 422);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Must be between -99,999 and 99,999."));

        Log.endTestCase("loadGroupDigiSep_25_CreateGroupWithBelowMinValueInRampInMinutes");
    }

    /**
     * This test case validates creation of DigiSep load group with above maximum value for rampInMinutes
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_26_CreateGroupWithAboveMaxValueInRampInMinutes() {
        Log.startTestCase("loadGroupDigiSep_26_CreateGroupWithAboveMaxValueInRampInMinutes");

        loadGroup.setRampInMinutes(100000);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", response.statusCode() == 422);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Must be between -99,999 and 99,999."));

        Log.endTestCase("loadGroupDigiSep_26_CreateGroupWithAboveMaxValueInRampInMinutes");
    }

    /**
     * This test case validates creation of DigiSep load group with below minimum value for rampOutMinutes
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_27_CreateGroupWithBelowMinVlaueInRampOutMinutes() {
        Log.startTestCase("loadGroupDigiSep_27_CreateGroupWithBelowMinVlaueInRampOutMinutes");

        loadGroup.setRampInMinutes(-100000);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", response.statusCode() == 422);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Must be between -99,999 and 99,999."));

        Log.endTestCase("loadGroupDigiSep_27_CreateGroupWithBelowMinVlaueInRampOutMinutes");
    }

    /**
     * This test case validates creation of DigiSep load group with above maximum value for rampOutMinutes
     */
    @Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
    public void loadGroupDigiSep_28_CreateWithAboveMaxValInRampOutMinutes() {
        Log.startTestCase("loadGroupDigiSep_28_CreateWithAboveMaxValInRampOutMinutes");

        loadGroup.setRampInMinutes(100000);

        ExtractableResponse<?> response = ApiCallHelper.post("saveloadgroup", loadGroup);
        assertTrue("Status code should be 422", response.statusCode() == 422);
        MockApiError error = response.as(MockApiError.class);
        assertTrue("Expected message should be - Validation error", error.getMessage().equals("Validation error"));
        List<MockApiFieldError> fieldErrors = error.getFieldErrors();
        String code = fieldErrors.get(0).getCode();
        assertTrue("Expected code in response is not correct", code.equals("Must be between -99,999 and 99,999."));

        Log.startTestCase("loadGroupDigiSep_28_CreateWithAboveMaxValInRampOutMinutes");
    }

	/**
	 * Negative validation when Load Group is copied with invalid Route Id
	 */
	@Test(dependsOnMethods = "loadGroupDigiSep_01_Create")
	public void loadGroupDigiSep_29_CopyWithInvalidRouteId(ITestContext context) {

		MockLoadGroupCopy loadGroupCopy = MockLoadGroupCopy.builder()
				.name(LoadGroupHelper.getCopiedLoadGroupName(MockPaoType.LM_GROUP_DIGI_SEP)).build();
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
     * Delete all the test data and load programs created for Direct program test methods.
     */
    @AfterClass
    public void tearDown(ITestContext context) {

        SoftAssert softAssert = new SoftAssert();
        MockLMDto deleteObject = MockLMDto.builder().build();

        // Delete LoadGroups which have been created for Load Group validations for positive scenarios
        for (Map.Entry<Integer, String> map : groups.entrySet()) {
            deleteObject.setName(map.getValue().toString());
            ExtractableResponse<?> response1 = ApiCallHelper.delete("deleteloadgroup", deleteObject, map.getKey().toString());
            softAssert.assertTrue(response1.statusCode() == 200, "Status code should be 200.");
        }

        softAssert.assertAll();
    }

}
