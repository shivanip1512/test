package com.cannontech.rest.api.dr.loadprogram;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import io.restassured.response.ExtractableResponse;

public class DirectProgramApiTest {

    private Integer programId = null;
    private MockLoadProgram mockLoadProgram = null;
    private HashMap<Integer, String> programs = new HashMap<Integer, String>();
    private HashMap<Integer, String> groups = new HashMap<Integer, String>();
    private HashMap<Integer, String> constraints = new HashMap<Integer, String>();

    /**
     * Test case is to create Direct Load Program for Direct Load Program create request.
     */
    @Test(description = "Create Direct load program")
    public void DirectProgram_01_Create(ITestContext context) {

        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_EMETCON);
        context.setAttribute("loadGroupDirect", loadGroup);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroup);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID, programConstraint.getId());
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME, programConstraint.getName());
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SmartCycle);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM, loadGroups,
                gearTypes, programConstraint.getId());
        loadProgram.setName("Auto_LmHoneywellProgramTest");
        loadProgram.setNotification(null);
        ExtractableResponse<?> response = ApiCallHelper.post("saveLoadProgram", loadProgram);
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME, loadProgram.getName());
        programId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID);
        assertTrue(response.statusCode() == 200, "Status code should be 200. Actual status code : " + response.statusCode());
        assertTrue(programId != null, "Program Id should not be Null");
        loadProgram.setProgramId(programId);
        context.setAttribute("expectedloadProgram", loadProgram);
    }

    /**
     * Test case is to get Direct Load Program created by test case DirectProgram_01_Create and validate created load
     * program fields in response are as expected.
     */
    @Test(dependsOnMethods = { "DirectProgram_01_Create" })
    public void DirectProgram_02_Get(ITestContext context) {

        MockLoadProgram expectedLoadProgram = (MockLoadProgram) context.getAttribute("expectedloadProgram");
        ExtractableResponse<?> response = ApiCallHelper.get("getLoadProgram", programId.toString());
        assertTrue(response.statusCode() == 200, "Status code should be 200");
        MockLoadProgram actualLoadProgram = response.as(MockLoadProgram.class);

        assertTrue(expectedLoadProgram.getName().equals(actualLoadProgram.getName()), "Expected and actual name is different");
        assertTrue(expectedLoadProgram.getType().equals(actualLoadProgram.getType()), "Expected and actual type is different");
        assertTrue(expectedLoadProgram.getGears().get(0).getGearName().equals(actualLoadProgram.getGears().get(0).getGearName()),
                "Expected and actual gear name is different");
        assertTrue(expectedLoadProgram.getGears().get(0).getControlMethod()
                .equals(actualLoadProgram.getGears().get(0).getControlMethod()),
                "Expected and actual gear control method is different");
    }

    /**
     * Test case is to update Load Program created by test case DirectProgram_01_Create and validate updated data in
     * response.
     */
    @Test(dependsOnMethods = { "DirectProgram_01_Create" })
    public void DirectProgram_03_Update(ITestContext context) {

        String name = "Auto_LmDirectProgram_Update";
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
    }

    /**
     * Test case is to create copy of Direct Load Program created by test case DirectProgram_01_Create
     */
    @Test(dependsOnMethods = { "DirectProgram_01_Create" })
    public void DirectProgram_04_Copy(ITestContext context) {
        MockLoadProgramCopy loadProgramCopy = LoadProgramSetupHelper.buildLoadProgramCopyRequest(MockPaoType.LM_DIRECT_PROGRAM,
                (Integer) context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID));

        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyLoadProgram", loadProgramCopy, programId.toString());
        assertTrue(copyResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(copyResponse.path("programId") != null, "Program Id should not be Null");
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME, loadProgramCopy.getName());
        programs.put(copyResponse.path("programId"), loadProgramCopy.getName());
    }

    /**
     * Test case is to delete the Direct Load Program created by test case DirectProgram_01_Create
     */
    @Test(dependsOnMethods = { "DirectProgram_01_Create" })
    public void DirectProgram_05_Delete(ITestContext context) {

        MockLMDto deleteObject = MockLMDto.builder()
                .name((String) context.getAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME)).build();

        ExtractableResponse<?> response = ApiCallHelper.delete("deleteLoadProgram", deleteObject, programId.toString());
        assertTrue(response.statusCode() == 200, "Status code should be 200");
        assertTrue(response.path("programId").equals(programId), "Expected programId to be deleted is not correct.");
    }

    /**
     * TTest case is to create Direct Load Program with all supported Gear Cycles provided via data provider
     */
    @Test(description = "Create Direct load program with supported gears", dataProvider = "GearCycleData")
    public void DirectProgram_06_CreateWithDifferentGears(MockGearControlMethod gearCycle, ITestContext context) {

        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_EMETCON);
        context.setAttribute("loadGroupDirect", loadGroup);
        groups.put(loadGroup.getId(), loadGroup.getName());
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroup);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        constraints.put(programConstraint.getId(), programConstraint.getName());

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(gearCycle);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());
        loadProgram.setName("HoneywellProgramTest_" + gearCycle + "4");
        loadProgram.setNotification(null);
        ExtractableResponse<?> response = ApiCallHelper.post("saveLoadProgram", loadProgram);
        assertTrue(response.statusCode() == 200, "Status code should be 200. Actual status code : " + response.statusCode());
        assertTrue(response.path("programId") != null, "Program Id should not be Null");

        loadProgram.setProgramId(response.path("programId"));
        programs.put(loadProgram.getProgramId(), loadProgram.getName());
        context.setAttribute("expectedloadProgram", loadProgram);
    }

    @DataProvider(name = "GearCycleData")
    public Object[][] getGearCycle(ITestContext context) {

        return new Object[][] { { MockGearControlMethod.TimeRefresh },
                { MockGearControlMethod.TargetCycle },
                { MockGearControlMethod.MasterCycle },
                { MockGearControlMethod.Rotation },
                { MockGearControlMethod.Latching },
                { MockGearControlMethod.TrueCycle },
                { MockGearControlMethod.MagnitudeCycle },
                { MockGearControlMethod.ThermostatRamping },
                { MockGearControlMethod.SimpleThermostatRamping },
                { MockGearControlMethod.BeatThePeak }
        };
    }

    /**
     * Test case to validate Load Program cannot be created with empty name and gets valid error message in response
     */
    @Test
    public void DirectProgram_07_NameCannotBeEmpty() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName(" ");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Name is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created with Program name having more than 60 characters and
     * validates valid error message in response
     */
    @Test
    public void DirectProgram_08_NameGreaterThanMaxLength() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName("TestNameMoreThanSixtyCharacter_TestNameMoreThanSixtyCharacter");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Exceeds maximum length of 60."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created with Program name having special characters and validates
     * valid error message in response
     */
    @Test
    public void DirectProgram_09_NameWithSpecialChars() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName("Test,//Test");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(
                createResponse.path("fieldErrors.code[0]")
                        .equals("Cannot be blank or include any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created with Program name already existing and validates valid error
     * message in response
     */
    @Test(dependsOnMethods = { "DirectProgram_04_Copy" })
    public void DirectProgram_10_CreateWithExistingProgramName(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName(context.getAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME).toString());
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Name must be unique."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created with trigger offset value less than 0 and validates valid
     * error message in response
     */
    @Test
    public void DirectProgram_11_NegativeTriggerOffset(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setTriggerOffset((double) -1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Must be between 0 and 100,000."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created with trigger offset value greater than 5 digits and
     * validates valid error message in response
     */
    @Test
    public void DirectProgram_12_TriggerOffsetGreaterThanMaxValue(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setTriggerOffset((double) 100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Must be between 0 and 100,000."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created with Restore offset value less than -9999.9999 and validates
     * valid error message in response
     */
    @Test
    public void DirectProgram_13_RestoreOffsetLessThanMinValue(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setRestoreOffset((double) -10000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Must be between -10,000 and 100,000."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created with Restore offset value more than 99999.9999 and validates
     * valid error message in response
     */
    @Test
    public void DirectProgram_14_RestoreOffsetGreaterThanMaxValue(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setRestoreOffset((double) 100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Must be between -10,000 and 100,000."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created without assigned load group and validates valid error
     * message in response
     */
    @Test
    public void DirectProgram_15_CreateWithoutAssignedGroup() {

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
    }

    /**
     * Test case to validate Load Program cannot be created without Gear and validates valid error message in response
     */
    @Test
    public void DirectProgram_16_CreateWithoutGear() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setGears(null);
        mockLoadProgram.setNotification(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("globalErrors.code[0]").equals("Program must contain 1 or more gears."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created without Program constraint and validates valid error message
     * in response
     */
    @Test
    public void DirectProgram_17_CreateWithoutConstraint() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setConstraint(null);
        mockLoadProgram.setNotification(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[0]").equals("Program Constraint is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created without Gear Name and validates valid error message in
     * response
     */
    @Test
    public void DirectProgram_18_CreateWithoutGearName() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getGears().get(0).setGearName("");
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Gear Name is required."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Control Window Start time cannot be less than 0 and validates valid error message in
     * response
     */
    @Test
    public void DirectProgram_19_ControlWindowStartTimeLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStartTimeInMinutes(-1);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Must be between 0 and 1,439."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created ControlWindow Start time less than 0 and validates valid
     * error message in response
     */
    @Test
    public void DirectProgram_20_ControlWindowStopTimeLessThanMinValue() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStopTimeInMinutes(-1);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Must be between 0 and 1,440."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created ControlWindow Start time greater than max value and
     * validates valid error message in response
     */
    @Test
    public void DirectProgram_21_ControlWindowStartTimeGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStartTimeInMinutes(1440);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Must be between 0 and 1,439."),
                "Expected code in response is not correct");
    }

    /**
     * Test case to validate Load Program cannot be created ControlWindow Start time greater than max value and
     * validates valid error message in response
     */
    @Test
    public void DirectProgram_22_ControlWindowStopTimeGreaterThanMaxValue() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getControlWindow().getControlWindowOne().setAvailableStopTimeInMinutes(1441);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(createResponse.path("message").equals("Validation error"), "Expected message should be - Validation error");
        assertTrue(createResponse.path("fieldErrors.code[1]").equals("Must be between 0 and 1,440."),
                "Expected code in response is not correct");
    }

    /**
     * Delete all the test data and load programs created for Direct program test methods.
     */
    @AfterClass
    public void tearDown(ITestContext context) {

        SoftAssert softAssert = new SoftAssert();
        MockLMDto deleteObject = MockLMDto.builder().build();

        // Delete LoadPrograms
        for (Map.Entry<Integer, String> map : programs.entrySet()) {
            deleteObject.setName(map.getValue().toString());
            ExtractableResponse<?> response = ApiCallHelper.delete("deleteLoadProgram", deleteObject, map.getKey().toString());
            softAssert.assertTrue(response.statusCode() == 200, "Status code should be 200. Delete copied LoadProgram failed.");
        }

        // Delete LoadGroups which have been created for Load Programs
        for (Map.Entry<Integer, String> map : groups.entrySet()) {
            deleteObject.setName(map.getValue().toString());
            ExtractableResponse<?> response1 = ApiCallHelper.delete("deleteloadgroup", deleteObject, map.getKey().toString());
            softAssert.assertTrue(response1.statusCode() == 200, "Status code should be 200. Delete LoadGroup failed.");
        }

        // Delete Program Constraints which have been created for Load Programs
        for (Map.Entry<Integer, String> map : constraints.entrySet()) {
            deleteObject.setName(map.getValue().toString());
            ExtractableResponse<?> response2 = ApiCallHelper.delete("deleteProgramConstraint",
                    deleteObject, map.getKey().toString());
            softAssert.assertTrue(response2.statusCode() == 200, "Status code should be 200. Delete Program Constraint failed.");
        }
        softAssert.assertAll();
    }

    /**
     * This is to build Mock LoadProgram payload to be used for negative scenarios test cases
     */
    public MockLoadProgram buildMockLoadProgram() {

        MockLoadGroupBase loadGroup = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_EMETCON);
        loadGroup.setId(3333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroup);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.SmartCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());

        return loadProgram;
    }

}
