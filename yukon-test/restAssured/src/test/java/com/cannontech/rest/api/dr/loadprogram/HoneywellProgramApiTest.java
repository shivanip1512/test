package com.cannontech.rest.api.dr.loadprogram;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;

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
import io.restassured.response.ExtractableResponse;

public class HoneywellProgramApiTest {

    private Integer programId = null;
    private Integer copyProgramId = null;
    private MockLoadProgram mockLoadProgram = null;

    /**
     * Test case is to create Honeywell Load Program for Honeywell Load Program create request.
     */
    @Test()
    public void HoneywellProgram_01_Create(ITestContext context) {

        MockLoadGroupBase loadGroupHoneywell = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_HONEYWELL);
        context.setAttribute("loadGroupHoneywell", loadGroupHoneywell);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupHoneywell);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        context.setAttribute("constraintId", programConstraint.getId());
        context.setAttribute("constraintName", programConstraint.getName());
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.HoneywellCycle);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_HONEYWELL_PROGRAM,
                                                                                     loadGroups,
                                                                                     gearTypes,
                                                                                     programConstraint.getId());
        loadProgram.setName("Auto_LmHoneywellProgramTest");
        loadProgram.setNotification(null);
        ExtractableResponse<?> response = ApiCallHelper.post("saveLoadProgram", loadProgram);
        context.setAttribute("programName", loadProgram.getName());
        programId = response.path("programId");
        assertTrue("Status code should be 200", response.statusCode() == 200);
        assertTrue("Program Id should not be Null", programId != null);
        loadProgram.setProgramId(programId);
        context.setAttribute("expectedloadProgram", loadProgram);
    }

    /**
     * Test case is to get Honeywell Load Program created by test case HoneywellProgram_01_Create and validate created
     * load program fields in response are as expected.
     */
    @Test(dependsOnMethods = { "HoneywellProgram_01_Create" })
    public void HoneywellProgram_02_Get(ITestContext context) {

        MockLoadProgram expectedLoadProgram = (MockLoadProgram) context.getAttribute("expectedloadProgram");
        ExtractableResponse<?> response = ApiCallHelper.get("getLoadProgram", programId.toString());

        assertTrue("Status code should be 200", response.statusCode() == 200);
        MockLoadProgram actualLoadProgram = response.as(MockLoadProgram.class);

        assertTrue("Expected and actual name is different", expectedLoadProgram.getName().equals(actualLoadProgram.getName()));
        assertTrue("Expected and actual type is different", expectedLoadProgram.getType().equals(actualLoadProgram.getType()));
        assertTrue("Expected and actual gear name is different",
                   expectedLoadProgram.getGears().get(0).getGearName().equals(actualLoadProgram.getGears().get(0).getGearName()));
        assertTrue("Expected and actual gear control method is different",
                   expectedLoadProgram.getGears().get(0).getControlMethod().equals(actualLoadProgram.getGears().get(0).getControlMethod()));
    }

    /**
     * Test case is to update Load Program created by test case HoneywellProgram_01_Create and validate updated data in
     * response.
     */
    @Test(dependsOnMethods = { "HoneywellProgram_01_Create" })
    public void HoneywellProgram_03_Update(ITestContext context) {

        String name = "Auto_LmHoneywellProgram_Update";
        String gearName = "TestGearUpdate";
        MockLoadProgram updateLoadProgram = (MockLoadProgram) context.getAttribute("expectedloadProgram");
        updateLoadProgram.setName(name);
        updateLoadProgram.getGears().get(0).setGearName(gearName);

        ExtractableResponse<?> response = ApiCallHelper.post("updateLoadProgram", updateLoadProgram, programId.toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        ExtractableResponse<?> getUpdatedResponse = ApiCallHelper.get("getLoadProgram", programId.toString());
        assertTrue("Status code should be 200", getUpdatedResponse.statusCode() == 200);
        MockLoadProgram updatedLoadProgram = getUpdatedResponse.as(MockLoadProgram.class);
        assertTrue("Name should be " + name, updatedLoadProgram.getName().equals(name));
        assertTrue("Gear Name should be " + gearName, updatedLoadProgram.getGears().get(0).getGearName().equals(gearName));
        context.setAttribute("programName", updatedLoadProgram.getName());
    }

    /**
     * Test case is to create copy of Honeywell Load Program created by test case HoneywellProgram_01_Create
     */
    @Test(dependsOnMethods = { "HoneywellProgram_01_Create" })
    public void HoneywellProgram_04_Copy(ITestContext context) {
        MockLoadProgramCopy loadProgramCopy = LoadProgramSetupHelper.buildLoadProgramCopyRequest(MockPaoType.LM_HONEYWELL_PROGRAM,
                                                                                                 (Integer) context.getAttribute("constraintId"));

        ExtractableResponse<?> copyResponse = ApiCallHelper.post("copyLoadProgram", loadProgramCopy, programId.toString());
        assertTrue("Status code should be 200", copyResponse.statusCode() == 200);
        assertTrue("Program Id should not be Null", copyResponse.path("programId") != null);
        context.setAttribute("copiedProgramName", loadProgramCopy.getName());
        copyProgramId = copyResponse.path("programId");
    }

    /**
     * Test case is to delete the Honeywell Load Program created by test case HoneywellProgram_01_Create
     */
    @Test(dependsOnMethods = { "HoneywellProgram_01_Create" })
    public void HoneywellProgram_05_Delete(ITestContext context) {

        MockLMDto deleteObject = MockLMDto.builder().name((String) context.getAttribute("programName")).build();

        ExtractableResponse<?> response = ApiCallHelper.delete("deleteLoadProgram", deleteObject, programId.toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
        assertTrue("Expected programId to be deleted is not correct.", response.path("programId").equals(programId));
    }

    /**
     * Test case to validate Load Program cannot be created with empty name and gets valid error message in response
     */
    @Test
    public void HoneywellProgram_06_NameCannotBeEmpty() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName(" ");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("fieldErrors.code[0]").equals("Name is required."));
    }

    /**
     * Test case to validate Load Program cannot be created with Program name having more than 60 characters and
     * validates valid error message in response
     */
    @Test
    public void HoneywellProgram_07_NameGreaterThanMaxLength() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName("TestNameMoreThanSixtyCharacter_TestNameMoreThanSixtyCharacter");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("fieldErrors.code[0]").equals("Exceeds maximum length of 60."));
    }

    /**
     * Test case to validate Load Program cannot be created with Program name having special characters and validates
     * valid error message in response
     */
    @Test
    public void HoneywellProgram_08_NameWithSpecialChars() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName("Test,//Test");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct",
                   createResponse.path("fieldErrors.code[0]").equals("Cannot be blank or include any of the following characters: / \\ , ' \" |"));
    }

    /**
     * Test case to validate Load Program cannot be created with Program name already existing and validates valid error
     * message in response
     */
    @Test(dependsOnMethods = { "HoneywellProgram_04_Copy" })
    public void HoneywellProgram_09_CreateWithExistingProgramName(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName(context.getAttribute("copiedProgramName").toString());
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("fieldErrors.code[0]").equals("Name must be unique."));
    }

    /**
     * Test case to validate Load Program cannot be created with trigger offset value less than 0 and validates valid
     * error message in response
     */
    @Test
    public void HoneywellProgram_10_NegativeTriggerOffset(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setTriggerOffset((double) -1);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("fieldErrors.code[0]").equals("Must be between 0 and 100,000."));
    }

    /**
     * Test case to validate Load Program cannot be created with trigger offset value greater than 5 digits and
     * validates valid error message in response
     */
    @Test
    public void HoneywellProgram_11_TriggerOffsetGreaterThanMaxValue(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setTriggerOffset((double) 100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("fieldErrors.code[0]").equals("Must be between 0 and 100,000."));
    }

    /**
     * Test case to validate Load Program cannot be created with Restore offset value less than -9999.9999 and validates
     * valid error message in response
     */
    @Test
    public void HoneywellProgram_12_RestoreOffsetLessThanMinValue(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setRestoreOffset((double) -10000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("fieldErrors.code[0]").equals("Must be between -10,000 and 100,000."));
    }

    /**
     * Test case to validate Load Program cannot be created with Restore offset value more than 99999.9999 and validates
     * valid error message in response
     */
    @Test
    public void HoneywellProgram_13_RestoreOffsetGreaterThanMaxValue(ITestContext context) {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setRestoreOffset((double) 100000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("fieldErrors.code[0]").equals("Must be between -10,000 and 100,000."));
    }

    /**
     * Test case to validate Load Program cannot be created without assigned load group and validates valid error
     * message in response
     */
    @Test
    public void HoneywellProgram_14_CreateWithoutAssignedGroup() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setAssignedGroups(null);
        mockLoadProgram.setNotification(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct",
                   createResponse.path("globalErrors.code[0]").equals("At least 1 load group must be present in this current program."));
    }

    /**
     * Test case to validate Load Program cannot be created without Gear and validates valid error message in response
     */
    @Test
    public void HoneywellProgram_15_CreateWithoutGear() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setGears(null);
        mockLoadProgram.setNotification(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("globalErrors.code[0]").equals("Program must contain 1 or more gears."));
    }

    /**
     * Test case to validate Load Program cannot be created without Program constraint and validates valid error message
     * in response
     */
    @Test
    public void HoneywellProgram_16_CreateWithoutConstraint() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setConstraint(null);
        mockLoadProgram.setNotification(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("fieldErrors.code[0]").equals("Program Constraint is required."));
    }

    /**
     * Test case to validate Load Program cannot be created without Gear Name and validates valid error message in
     * response
     */
    @Test
    public void HoneywellProgram_17_CreateWithoutGearName() {

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.getGears().get(0).setGearName("");
        mockLoadProgram.setNotification(null);
        
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be 422", createResponse.statusCode() == 422);
        assertTrue("Expected message should be - Validation error", createResponse.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", createResponse.path("fieldErrors.code[1]").equals("Gear Name is required."));
    }

    /**
     * Delete all the test data and load programs created for honeywell program test methods.
     */
    @AfterClass
    public void tearDown(ITestContext context) {

        SoftAssert softAssert = new SoftAssert();
        // Delete Copied LoadProgram
        MockLMDto deleteObject = MockLMDto.builder().name((String) context.getAttribute("copiedProgramName")).build();
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteLoadProgram", deleteObject, copyProgramId.toString());
        softAssert.assertTrue(response.statusCode() == 200, "Status code should be 200. Delete copied LoadProgram failed.");

        // Delete LoadGroup which have been created for Load Program
        MockLoadGroupBase loadGroup = (MockLoadGroupBase) context.getAttribute("loadGroupHoneywell");
        deleteObject.setName(loadGroup.getName());
        ExtractableResponse<?> response1 = ApiCallHelper.delete("deleteloadgroup", deleteObject, loadGroup.getId().toString());
        softAssert.assertTrue(response1.statusCode() == 200, "Status code should be 200. Delete LoadGroup failed.");

        // Delete Program Constraint which have been created for Load Program
        MockLMDto deleteConstraint = MockLMDto.builder().name(context.getAttribute("constraintName").toString()).build();
        ExtractableResponse<?> response2 = ApiCallHelper.delete("deleteProgramConstraint", deleteConstraint, context.getAttribute("constraintId").toString());
        softAssert.assertTrue(response2.statusCode() == 200, "Status code should be 200. Delete Program Constraint failed.");

        softAssert.assertAll();
    }

    /**
     * This is to build Mock LoadProgram payload to be used for negative scenarios test cases
     */
    public MockLoadProgram buildMockLoadProgram() {

        MockLoadGroupBase loadGroupHoneywell = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_HONEYWELL);
        loadGroupHoneywell.setId(3333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupHoneywell);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.HoneywellCycle);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_HONEYWELL_PROGRAM,
                                                                                     loadGroups,
                                                                                     gearTypes,
                                                                                     programConstraint.getId());

        return loadProgram;
    }

}
