package com.cannontech.rest.api.dr.loadprogram;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
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
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class SepProgramApiTest {

    private Integer programId = null;
    private Integer copyProgramId = null;
    private MockLoadProgram mockLoadProgram = null;

    /**
     * Test case is to create SEP Load Program for SEP Load Program create request.
     */
    @Test
    public void SepProgram_01_Create(ITestContext context) {

        Log.startTestCase("SepProgram_01_Create");
        MockLoadGroupBase loadGroupDigiSep = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        context.setAttribute("loadGroupDigiSep", loadGroupDigiSep);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupDigiSep);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID, programConstraint.getId());
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME, programConstraint.getName());
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SepCycle);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_SEP_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());
        loadProgram.setName("Auto_LmSepProgramTest");
        loadProgram.setNotification(null);
        // Create Load Program
        ExtractableResponse<?> response = ApiCallHelper.post("saveLoadProgram", loadProgram);
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME, loadProgram.getName());
        programId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID);
        assertTrue(response.statusCode() == 200, "Status code should be 200");
        assertTrue(programId != null, "Program Id should not be Null");
        loadProgram.setProgramId(programId);
        context.setAttribute("expectedloadProgram", loadProgram);
        Log.endTestCase("SepProgram_01_Create");
    }

    /**
     * Test case is to get SEP Load Program created by test case SepProgram_01_Create and validate created load program
     * fields in response are as expected.
     */

    @Test(dependsOnMethods = "SepProgram_01_Create")
    public void SepProgram_02_Get(ITestContext context) {

        Log.startTestCase("SepProgram_02_Get");
        MockLoadProgram expectedLoadProgram = (MockLoadProgram) context.getAttribute("expectedloadProgram");
        ExtractableResponse<?> response = ApiCallHelper.get("getLoadProgram", programId.toString());
        assertTrue(response.statusCode() == 200, "Status code should be 200");
        MockLoadProgram actualLoadProgram = response.as(MockLoadProgram.class);
        assertTrue(expectedLoadProgram.getName().equals(actualLoadProgram.getName()), "Expected and actual name is different");
        assertTrue(expectedLoadProgram.getType().equals(actualLoadProgram.getType()), "Expected and actual type is different");
        assertTrue(expectedLoadProgram.getGears().get(0).getGearName().equals(actualLoadProgram.getGears().get(0).getGearName()),
                "Expected and actual gear name is different");
        assertTrue(
                expectedLoadProgram.getGears().get(0).getControlMethod()
                        .equals(actualLoadProgram.getGears().get(0).getControlMethod()),
                "Expected and actual gear control method is different");
        Log.endTestCase("SepProgram_02_Get");
    }

    /**
     * Test case is to update Load Program and validate updated data in response.
     */

    @Test(dependsOnMethods = "SepProgram_02_Get")
    public void SepProgram_03_Update(ITestContext context) {

        Log.startTestCase("SepProgram_03_Update");
        String name = "Auto_LmSepProgram_Update";
        String gearName = "TestGearUpdate";
        MockLoadProgram updateLoadProgram = (MockLoadProgram) context.getAttribute("expectedloadProgram");
        updateLoadProgram.setName(name);
        updateLoadProgram.getGears().get(0).setGearName(gearName);

        ExtractableResponse<?> response = ApiCallHelper.post("updateLoadProgram", updateLoadProgram, programId.toString());
        assertTrue(response.statusCode() == 200, "Status code should be 200");

        ExtractableResponse<?> getUpdatedResponse = ApiCallHelper.get("getLoadProgram", programId.toString());
        assertTrue(getUpdatedResponse.statusCode() == 200, "Status code should be 200");
        MockLoadProgram updatedLoadProgram = getUpdatedResponse.as(MockLoadProgram.class);
        assertTrue(updatedLoadProgram.getName().equals(name), "Name should be " + name);
        assertTrue(updatedLoadProgram.getGears().get(0).getGearName().equals(gearName), "Gear Name should be " + gearName);
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME, updatedLoadProgram.getName());
        Log.endTestCase("SepProgram_03_Update");
    }

    /**
     * Test case is to create copy of SEP Load Program created by test case SepProgram_01_Create
     */

    @Test(dependsOnMethods = "SepProgram_03_Update")
    public void SepProgram_04_Copy(ITestContext context) {

        Log.startTestCase("SepProgram_04_Copy");
        MockLoadProgramCopy loadProgramCopy = LoadProgramSetupHelper.buildLoadProgramCopyRequest(MockPaoType.LM_SEP_PROGRAM,
                (Integer) context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID));
        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyLoadProgram", loadProgramCopy, programId.toString());
        assertTrue(copyResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(copyResponse.path("programId") != null, "Program Id should not be Null");

        context.setAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME, loadProgramCopy.getName());
        copyProgramId = copyResponse.path("programId");
        Log.endTestCase("SepProgram_04_Copy");
    }

    /**
     * Test case to validate Load Program cannot be created with Program name already existing and validates valid error
     * message in response
     */

    @Test(dependsOnMethods = "SepProgram_04_Copy")
    public void SepProgram_05_CreateWithExistingProgramName(ITestContext context) {

        Log.startTestCase("SepProgram_05_CreateWithExistingProgramName");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName(context.getAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME).toString());

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Name must be unique."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_05_CreateWithExistingProgramName");
    }

    /**
     * Test case is to delete the Sep Load Program created by test case SepProgram_01_Create
     */

    @Test(dependsOnMethods = "SepProgram_05_CreateWithExistingProgramName")
    public void SepProgram_06_Delete(ITestContext context) {

        Log.startTestCase("SepProgram_06_Delete");
        MockLMDto deleteObject = MockLMDto.builder()
                .name((String) context.getAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME)).build();
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteLoadProgram", deleteObject, programId.toString());
        assertTrue(response.statusCode() == 200, "Status code should be 200");
        assertTrue(response.path("programId").equals(programId), "Expected programId to be deleted is not correct");
        Log.startTestCase("SepProgram_06_Delete");
    }

    /**
     * This Test case is to create Sep Load Program with all supported Gear Cycles provided via data provider
     */
    @Test(description = "Create Sep load program with supported gears", dataProvider = "GearCycleData")
    public void SepProgram_07_CreateWithDifferentGears(MockGearControlMethod gearCycle, ITestContext context) {

        Log.startTestCase("SepProgram_07_CreateWithDifferentGears");
        MockLoadGroupBase loadGroupDigiSep = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        context.setAttribute("loadGroupDigiSep", loadGroupDigiSep);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupDigiSep);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID, programConstraint.getId());
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME, programConstraint.getName());
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(gearCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_SEP_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());
        loadProgram.setName("Auto_LmSepProgramTest_" + gearCycle);
        loadProgram.setNotification(null);
        ExtractableResponse<?> response = ApiCallHelper.post("saveLoadProgram", loadProgram);
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME, loadProgram.getName());

        assertTrue(response.statusCode() == 200, "Status code should be 200. Actual status code : " + response.statusCode());
        assertTrue(response.path("programId") != null, "Program Id should not be Null");
        loadProgram.setProgramId(response.path("programId"));

        /// To delete load programs created with different gears
        MockLMDto deleteObject = MockLMDto.builder()
                .name((String) context.getAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME)).build();
        ExtractableResponse<?> deleteLdPrgmResponse = ApiCallHelper.delete("deleteLoadProgram", deleteObject,
                response.path("programId").toString());
        assertTrue(deleteLdPrgmResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(deleteLdPrgmResponse.path("programId").equals(response.path("programId")),
                "Expected programId to be deleted is not correct");
        Log.endTestCase("SepProgram_07_CreateWithDifferentGears");

    }

    /**
     * DataProvider provides GearsName data to test method in the form of object array Data provided
     */

    @DataProvider(name = "GearCycleData")
    public Object[][] getGearCycle(ITestContext context) {

        return new Object[][] { { MockGearControlMethod.SepTemperatureOffset },
                { MockGearControlMethod.NoControl },
        };
    }

    /**
     * Test case to validate Load Program cannot be created with empty name and gets valid error message in response
     */

    @Test
    public void SepProgram_08_NameCannotBeEmpty() {

        Log.startTestCase("SepProgram_08_NameCannotBeEmpty");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName(" ");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Name is required."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_08_NameCannotBeEmpty");
    }

    /**
     * Test case to validate Load Program cannot be created with Program name having more than 60 characters and
     * validates valid error message in response
     */

    @Test
    public void SepProgram_09_NameGreaterThanMaxLength() {

        Log.startTestCase("SepProgram_09_NameGreaterThanMaxLength");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName("TestNameMoreThanSixtyCharacter_TestNameMoreThanSixtyCharacter");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Exceeds maximum length of 60."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_09_NameGreaterThanMaxLength");
    }

    /**
     * Test case to validate Load Program cannot be created with Program name having special characters and validates
     * valid error message in response
     */

    @Test
    public void SepProgram_10_NameWithSpecialChars() {

        Log.startTestCase("SepProgram_10_NameWithSpecialChars");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName("Test,//Test");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(
                createResponse.path("fieldErrors.code[0]")
                        .equals("Cannot be blank or include any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_10_NameWithSpecialChars");
    }


    /**
     * Test case to validate Load Program cannot be created with trigger offset value less than 0 and validates valid
     * error message in response
     */

    @Test
    public void SepProgram_11_NegativeTriggerOffset(ITestContext context) {

        Log.startTestCase("SepProgram_11_NegativeTriggerOffset");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setTriggerOffset((double) -1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Must be between 0 and 100,000."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_11_NegativeTriggerOffset");
    }

    /**
     * Test case to validate Load Program cannot be created with trigger offset value greater than 5 digits and
     * validates valid error message in response
     */

    @Test
    public void SepProgram_12_TriggerOffsetGreaterThanMaxValue(ITestContext context) {

        Log.startTestCase("SepProgram_12_TriggerOffsetGreaterThanMaxValue");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setTriggerOffset((double) 100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Must be between 0 and 100,000."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_12_TriggerOffsetGreaterThanMaxValue");
    }

    /**
     * Test case to validate Load Program cannot be created with Restore offset value less than -9999.9999 and validates
     * valid error message in response
     */

    @Test
    public void SepProgram_13_RestoreOffsetLessThanMinValue(ITestContext context) {

        Log.startTestCase("SepProgram_13_RestoreOffsetLessThanMinValue");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setRestoreOffset((double) -10000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Must be between -10,000 and 100,000."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_13_RestoreOffsetLessThanMinValue");
    }

    /**
     * Test case to validate Load Program cannot be created with Restore offset value more than 99999.9999 and validates
     * valid error message in response
     */

    @Test
    public void SepProgram_14_RestoreOffsetGreaterThanMaxValue(ITestContext context) {

        Log.startTestCase("SepProgram_14_RestoreOffsetGreaterThanMaxValue");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setRestoreOffset((double) 100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Must be between -10,000 and 100,000."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_14_RestoreOffsetGreaterThanMaxValue");
    }

    /**
     * Test case to validate Load Program cannot be created without assigned load group and validates valid error
     * message in response
     */

    @Test
    public void SepProgram_15_CreateWithoutAssignedGroup() {

        Log.startTestCase("SepProgram_15_CreateWithoutAssignedGroup");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setAssignedGroups(null);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(
                createResponse.path("globalErrors.code[0]")
                        .equals("At least 1 load group must be present in this current program."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_15_CreateWithoutAssignedGroup");
    }

    /**
     * Test case to validate Load Program cannot be created without Gear and validates valid error message in response
     */

    @Test
    public void SepProgram_16_CreateWithoutGear() {

        Log.startTestCase("SepProgram_16_CreateWithoutGear");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setGears(null);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("globalErrors.code[0]").equals("Program must contain 1 or more gears."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_16_CreateWithoutGear");
    }

    /**
     * Test case to validate Load Program cannot be created without Program constraint and validates valid error message
     * in response
     */

    @Test
    public void SepProgram_17_CreateWithoutConstraint() {

        Log.startTestCase("SepProgram_17_CreateWithoutConstraint");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setConstraint(null);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Program Constraint is required."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_17_CreateWithoutConstraint");
    }

    /**
     * Test case to validate Load Program cannot be created without Gear Name and validates valid error message in
     * response
     */

    @Test
    public void SepProgram_18_CreateWithoutGearName() {

        Log.startTestCase("SepProgram_18_CreateWithoutGearName");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getGears().get(0).setGearName("");
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Gear Name is required."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_18_CreateWithoutGearName");
    }

    /**
     * Test case to validate Control Window Start time cannot be less than 0 and validates valid error message in
     * response
     */

    @Test
    public void SepProgram_19_ControlWindowStartTimeLessThanMinValue() {

        Log.startTestCase("SepProgram_19_ControlWindowStartTimeLessThanMinValue");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStartTimeInMinutes(-1);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Must be between 0 and 1,439."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_19_ControlWindowStartTimeLessThanMinValue");
    }

    /**
     * Test case to validate Load Program cannot be created ControlWindow Start time less than 0 and validates valid
     * error message in response
     */

    @Test
    public void SepProgram_20_ControlWindowStopTimeLessThanMinValue() {

        Log.startTestCase("SepProgram_20_ControlWindowStopTimeLessThanMinValue");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStopTimeInMinutes(-1);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Must be between 0 and 1,440."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_20_ControlWindowStopTimeLessThanMinValue");
    }

    /**
     * Test case to validate Load Program cannot be created ControlWindow Start time greater than max value and
     * validates valid error message in response
     */

    @Test
    public void SepProgram_21_ControlWindowStartTimeGreaterThanMaxValue() {

        Log.startTestCase("SepProgram_21_ControlWindowStartTimeGreaterThanMaxValue");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStartTimeInMinutes(1440);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Must be between 0 and 1,439."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_21_ControlWindowStartTimeGreaterThanMaxValue");
    }

    /**
     * Test case to validate Load Program cannot be created ControlWindow Start time greater than max value and
     * validates valid error message in response
     */

    @Test
    public void SepProgram_22_ControlWindowStopTimeGreaterThanMaxValue() {

        Log.startTestCase("SepProgram_22_ControlWindowStopTimeGreaterThanMaxValue");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStopTimeInMinutes(1441);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Must be between 0 and 1,440."),
                "Expected code in response is not correct");
        Log.endTestCase("SepProgram_22_ControlWindowStopTimeGreaterThanMaxValue");
    }

    /**
     * Test case to validate Load Program cannot be created with null load group id and validates valid error
     * message in response
     */
    @Test
    public void SepProgram_23_CreateWithLoadGroupIdAsNull(ITestContext context) {

        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        context.setAttribute("loadGroupDirect", loadGroup);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroup);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID, programConstraint.getId());
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME, programConstraint.getName());
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SepCycle);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_SEP_PROGRAM, loadGroups,
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
     * Delete all the test data and load programs created for Sep program test methods.
     */

    @AfterClass
    public void tearDown(ITestContext context) {

        Log.startTestCase("tearDown");
        SoftAssert softAssert = new SoftAssert();

        // Delete Copied LoadProgram
        MockLMDto deleteObject = MockLMDto.builder()
                .name((String) context.getAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME)).build();
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteLoadProgram", deleteObject, copyProgramId.toString());
        softAssert.assertTrue(response.statusCode() == 200, "Status code should be 200. Delete copied LoadProgram failed.");

        // Delete LoadGroup which have been created for Load Program
        MockLoadGroupBase loadGroup = (MockLoadGroupBase) context.getAttribute("loadGroupDigiSep");
        deleteObject.setName(loadGroup.getName());
        ExtractableResponse<?> deleteLdGrpResponse = ApiCallHelper.delete("deleteloadgroup", deleteObject, loadGroup.getId().toString());
        softAssert.assertTrue(deleteLdGrpResponse.statusCode() == 200, "Status code should be 200. Delete LoadGroup failed.");

        // Delete Program Constraint which have been created for Load Program
        MockLMDto deleteConstraint = MockLMDto.builder()
                .name(context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME).toString()).build();
        ExtractableResponse<?> deletePrgmCnstResponse = ApiCallHelper.delete("deleteProgramConstraint",
                deleteConstraint,
                context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID).toString());
        softAssert.assertTrue(deletePrgmCnstResponse.statusCode() == 200, "Status code should be 200. Delete Program Constraint failed.");
        softAssert.assertAll();
        Log.endTestCase("tearDown");
    }

    /**
     * This is to build Mock LoadProgram payload to be used for negative scenarios test cases
     */
    public MockLoadProgram buildMockLoadProgram() {
        MockLoadGroupBase loadGroupDigiSep = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        loadGroupDigiSep.setId(3333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupDigiSep);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SepCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_SEP_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());
        return loadProgram;
    }
}