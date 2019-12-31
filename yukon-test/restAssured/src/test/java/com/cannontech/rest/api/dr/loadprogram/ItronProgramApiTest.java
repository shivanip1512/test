package com.cannontech.rest.api.dr.loadprogram;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgramCopy;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class ItronProgramApiTest {

    private Integer programId = null;
    private Integer copyProgramId = null;
    private MockLoadProgram mockLoadProgram = null;

    /**
     * Test case is to create Itron Load Program for Itron Load Program create
     * request.
     */
    @Test
    public void ItronProgram_01_Create(ITestContext context) {

        MockLoadGroupBase loadGroupItron = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_ITRON);
        context.setAttribute("loadGroupItron", loadGroupItron);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupItron);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID, programConstraint.getId());
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME, programConstraint.getName());
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.ItronCycle);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ITRON_PROGRAM,
                loadGroups, gearTypes, programConstraint.getId());
        loadProgram.setName("Auto_LmItronProgramTest");
        loadProgram.setNotification(null);
        ExtractableResponse<?> response = ApiCallHelper.post("saveLoadProgram", loadProgram);
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME, loadProgram.getName());
        programId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID);
        Assert.assertTrue(response.statusCode() == 200, "Status code should be 200");
        Assert.assertTrue(programId != null, "Program Id should not be Null");
        loadProgram.setProgramId(programId);
        context.setAttribute("expectedloadProgram", loadProgram);
    }

    /**
     * Test case is to get Itron Load Program created by test case
     * ItronProgram_01_Create and validate created load program fields in response
     * are as expected.
     */
    @Test(dependsOnMethods = { "ItronProgram_01_Create" })
    public void ItronProgram_02_Get(ITestContext context) {

        MockLoadProgram expectedLoadProgram = (MockLoadProgram) context.getAttribute("expectedloadProgram");
        ExtractableResponse<?> response = ApiCallHelper.get("getLoadProgram", programId.toString());

        Assert.assertTrue(response.statusCode() == 200, "Status code should be 200");
        MockLoadProgram actualLoadProgram = response.as(MockLoadProgram.class);

        Assert.assertTrue(expectedLoadProgram.getName().equals(actualLoadProgram.getName()),
                "Expected and actual name is different");
        Assert.assertTrue(expectedLoadProgram.getType().equals(actualLoadProgram.getType()),
                "Expected and actual type is different");
        Assert.assertTrue(
                expectedLoadProgram.getGears().get(0).getGearName()
                        .equals(actualLoadProgram.getGears().get(0).getGearName()),
                "Expected and actual gear name is different");
        Assert.assertTrue(
                expectedLoadProgram.getGears().get(0).getControlMethod()
                        .equals(actualLoadProgram.getGears().get(0).getControlMethod()),
                "Expected and actual gear control method is different");
    }

    /**
     * Test case is to update Load Program created by test case
     * ItronProgram_01_Create and validate updated data in response.
     */
    @Test(dependsOnMethods = { "ItronProgram_01_Create" })
    public void ItronProgram_03_Update(ITestContext context) {

        String name = "Auto_LmItronProgram_Update";
        String gearName = "TestGearUpdate";
        MockLoadProgram updateLoadProgram = (MockLoadProgram) context.getAttribute("expectedloadProgram");
        updateLoadProgram.setName(name);
        updateLoadProgram.getGears().get(0).setGearName(gearName);

        ExtractableResponse<?> response = ApiCallHelper.post("updateLoadProgram", updateLoadProgram,
                programId.toString());
        Assert.assertTrue(response.statusCode() == 200, "Status code should be 200");

        ExtractableResponse<?> getUpdatedResponse = ApiCallHelper.get("getLoadProgram", programId.toString());
        Assert.assertTrue(getUpdatedResponse.statusCode() == 200, "Status code should be 200");
        MockLoadProgram updatedLoadProgram = getUpdatedResponse.as(MockLoadProgram.class);
        Assert.assertTrue(updatedLoadProgram.getName().equals(name), "Name should be " + name);
        Assert.assertTrue(updatedLoadProgram.getGears().get(0).getGearName().equals(gearName),
                "Gear Name should be " + gearName);
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME, updatedLoadProgram.getName());
    }

    /**
     * Test case is to create copy of Itron Load Program created by test case
     * ItronProgram_01_Create
     */
    @Test(dependsOnMethods = { "ItronProgram_01_Create" })
    public void ItronProgram_04_Copy(ITestContext context) {
        MockLoadProgramCopy loadProgramCopy = LoadProgramSetupHelper.buildLoadProgramCopyRequest(
                MockPaoType.LM_ITRON_PROGRAM,
                (Integer) context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID));

        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyLoadProgram", loadProgramCopy,
                programId.toString());
        Assert.assertTrue(copyResponse.statusCode() == 200, "Status code should be 200");
        Assert.assertTrue(copyResponse.path("programId") != null, "Program Id should not be Null");
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME, loadProgramCopy.getName());
        copyProgramId = copyResponse.path("programId");
    }

    /**
     * Test case is to delete the Itron Load Program created by test case
     * ItronProgram_01_Create
     */
    @Test(dependsOnMethods = { "ItronProgram_01_Create" })
    public void ItronProgram_05_Delete(ITestContext context) {

        MockLMDto deleteObject = MockLMDto.builder()
                .name((String) context.getAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME)).build();

        ExtractableResponse<?> response = ApiCallHelper.delete("deleteLoadProgram", deleteObject, programId.toString());
        Assert.assertTrue(response.statusCode() == 200, "Status code should be 200");
        Assert.assertTrue(response.path("programId").equals(programId),
                "Expected programId to be deleted is not correct.");
    }

    /**
     * Test case to validate Load Program cannot be created with empty name and gets
     * valid error message in response
     */
    @Test
    public void ItronProgram_06_NameCannotBeEmpty() {

        String expectedErrorMsg = "Name is required.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName(" ");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "name", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created with Program name having
     * more than 60 characters and validates valid error message in response
     */
    @Test
    public void ItronProgram_07_NameGreaterThanMaxLength() {
        String expectedErrorMsg = "Exceeds maximum length of 60.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName("TestNameMoreThanSixtyCharacter_TestNameMoreThanSixtyCharacter");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "name", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);
    }

    /**
     * Test case to validate Load Program cannot be created with Program name having
     * special characters and validates valid error message in response
     */
    @Test
    public void ItronProgram_08_NameWithSpecialChars() {
        String expectedErrorMsg = "Cannot be blank or include any of the following characters: / \\ , ' \" |";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName("Test,//Test");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "name", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);
    }

    /**
     * Test case to validate Load Program cannot be created with Program name
     * already existing and validates valid error message in response
     */
    @Test(dependsOnMethods = { "ItronProgram_04_Copy" })
    public void ItronProgram_09_CreateWithExistingProgramName(ITestContext context) {
        String expectedErrorMsg = "Name must be unique.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName(context.getAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME).toString());
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "name", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created with trigger offset
     * value less than 0 and validates valid error message in response
     */
    @Test
    public void ItronProgram_10_NegativeTriggerOffset(ITestContext context) {
        String expectedErrorMsg = "Must be between 0 and 100,000.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setTriggerOffset((double) -1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "triggerOffset", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created with trigger offset
     * value greater than 5 digits and validates valid error message in response
     */
    @Test
    public void ItronProgram_11_TriggerOffsetGreaterThanMaxValue(ITestContext context) {
        String expectedErrorMsg = "Must be between 0 and 100,000.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setTriggerOffset((double) 100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "triggerOffset", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created with Restore offset
     * value less than -9999.9999 and validates valid error message in response
     */
    @Test
    public void ItronProgram_12_RestoreOffsetLessThanMinValue(ITestContext context) {
        String expectedErrorMsg = "Must be between -10,000 and 100,000.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setRestoreOffset((double) -10000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "restoreOffset", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);
    }

    /**
     * Test case to validate Load Program cannot be created with Restore offset
     * value more than 99999.9999 and validates valid error message in response
     */
    @Test
    public void ItronProgram_13_RestoreOffsetGreaterThanMaxValue(ITestContext context) {
        String expectedErrorMsg = "Must be between -10,000 and 100,000.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setRestoreOffset((double) 100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "restoreOffset", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created without assigned load
     * group and validates valid error message in response
     */
    @Test
    public void ItronProgram_14_CreateWithoutAssignedGroup() {
        String expectedErrorMsg = "At least 1 load group must be present in this current program.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setAssignedGroups(null);
        mockLoadProgram.setNotification(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateGlobalErrors(createResponse, expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created without Gear and
     * validates valid error message in response
     */
    @Test
    public void ItronProgram_15_CreateWithoutGear() {
        String expectedErrorMsg = "Program must contain 1 or more gears.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setGears(null);
        mockLoadProgram.setNotification(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateGlobalErrors(createResponse, expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created without Program
     * constraint and validates valid error message in response
     */
    @Test
    public void ItronProgram_16_CreateWithoutConstraint() {
        String expectedErrorMsg = "Program Constraint is required.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setConstraint(null);
        mockLoadProgram.setNotification(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "constraint", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created without Gear Name and
     * validates valid error message in response
     */
    @Test
    public void ItronProgram_17_CreateWithoutGearName() {
        String expectedErrorMsg = "Gear Name is required.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getGears().get(0).setGearName("");
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Control Window Start time cannot be less than 0 and
     * validates valid error message in response
     */
    @Test
    public void ItronProgram_18_ControlWindowStartTimeLessThanMinValue() {

        String expectedErrorMsg = "Must be between 0 and 1,439.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStartTimeInMinutes(-1);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(
                ValidationHelper.validateFieldError(createResponse,
                        "controlWindow.controlWindowOne.availableStartTimeInMinutes", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created ControlWindow Start time
     * less than 0 and validates valid error message in response
     */
    @Test
    public void ItronProgram_19_ControlWindowStopTimeLessThanMinValue() {

        String expectedErrorMsg = "Must be between 0 and 1,440.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStopTimeInMinutes(-1);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(
                ValidationHelper.validateFieldError(createResponse,
                        "controlWindow.controlWindowOne.availableStopTimeInMinutes", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created ControlWindow Start time
     * greater than max value and validates valid error message in response
     */
    @Test
    public void ItronProgram_20_ControlWindowStartTimeGreaterThanMaxValue() {
        String expectedErrorMsg = "Must be between 0 and 1,439.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStartTimeInMinutes(1440);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");

        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(
                ValidationHelper.validateFieldError(createResponse,
                        "controlWindow.controlWindowOne.availableStartTimeInMinutes", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);

    }

    /**
     * Test case to validate Load Program cannot be created ControlWindow Start time
     * greater than max value and validates valid error message in response
     */
    @Test
    public void ItronProgram_21_ControlWindowStopTimeGreaterThanMaxValue() {
        String expectedErrorMsg = "Must be between 0 and 1,440.";
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStopTimeInMinutes(1441);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        Assert.assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        Assert.assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message value is 'Validation error'");
        Assert.assertTrue(
                ValidationHelper.validateFieldError(createResponse,
                        "controlWindow.controlWindowOne.availableStopTimeInMinutes", expectedErrorMsg),
                "Expected Error not found:" + expectedErrorMsg);
    }

    /**
     * Test case to validate Load Program cannot be created with null load group id and validates valid error
     * message in response
     */
    @Test
    public void ItronProgram_22_CreateWithLoadGroupIdAsNull(ITestContext context) {

        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_ITRON);
        context.setAttribute("loadGroupDirect", loadGroup);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroup);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID, programConstraint.getId());
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME, programConstraint.getName());
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.ItronCycle);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ITRON_PROGRAM, loadGroups,
                gearTypes, programConstraint.getId());
        loadProgram.setName("Auto_ProgramTest");
        loadProgram.setNotification(null);
        loadProgram.getAssignedGroups().get(0).setGroupId(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", loadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "assignedGroups[0].groupId",
                "Group Id is required."),
                "Expected code in response is not correct");
    }

    /**
     * Delete all the test data and load programs created for Itron program test
     * methods.
     */
    @AfterClass
    public void tearDown(ITestContext context) {

        SoftAssert softAssert = new SoftAssert();
        // Delete Copied LoadProgram
        MockLMDto deleteObject = MockLMDto.builder()
                .name((String) context.getAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME)).build();
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteLoadProgram", deleteObject,
                copyProgramId.toString());
        softAssert.assertTrue(response.statusCode() == 200,
                "Status code should be 200. Delete copied LoadProgram failed.");

        // Delete LoadGroup which have been created for Load Program
        MockLoadGroupBase loadGroup = (MockLoadGroupBase) context.getAttribute("loadGroupItron");
        deleteObject.setName(loadGroup.getName());
        ExtractableResponse<?> response1 = ApiCallHelper.delete("deleteloadgroup", deleteObject,
                loadGroup.getId().toString());
        softAssert.assertTrue(response1.statusCode() == 200, "Status code should be 200. Delete LoadGroup failed.");

        // Delete Program Constraint which have been created for Load Program
        MockLMDto deleteConstraint = MockLMDto.builder()
                .name(context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME).toString()).build();
        ExtractableResponse<?> response2 = ApiCallHelper.delete("deleteProgramConstraint", deleteConstraint,
                context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID).toString());
        softAssert.assertTrue(response2.statusCode() == 200,
                "Status code should be 200. Delete Program Constraint failed.");

        softAssert.assertAll();
    }

    /**
     * This is to build Mock LoadProgram payload to be used for negative scenarios
     * test cases
     */
    public MockLoadProgram buildMockLoadProgram() {

        MockLoadGroupBase loadGroupItron = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ITRON);
        loadGroupItron.setId(3333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupItron);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.ItronCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ITRON_PROGRAM,
                loadGroups, gearTypes, programConstraint.getId());

        return loadProgram;
    }
}
