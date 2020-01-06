package com.cannontech.rest.api.dr.loadprogram;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
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

public class EcobeeProgramApiTest {

    private Integer programId = 0;
    private MockLoadProgram mockLoadProgram = null;

    @Test
    public void loadPgmEcobee_01_Create(ITestContext context) {
        Log.startTestCase("loadPgmEcobee_01_Create");
        MockLoadGroupBase loadGroupEcobee = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_ECOBEE);
        context.setAttribute("loadGroupEcobee", loadGroupEcobee);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupEcobee);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID, programConstraint.getId());
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME, programConstraint.getName());
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.EcobeeCycle);
        gearTypes.add(MockGearControlMethod.EcobeeSetpoint);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ECOBEE_PROGRAM,
                                                                                     loadGroups,
                                                                                     gearTypes,
                                                                                     programConstraint.getId());

        loadProgram.setName("Auto_LmEcobeeProgramTest");
        loadProgram.setNotification(null);
        // Create Load Program
        ExtractableResponse<?> response = ApiCallHelper.post("saveLoadProgram", loadProgram);
        assertTrue("Status code should be 200", response.statusCode() == 200);
        assertTrue("GROUP Id should not be Null", response.path("programId") != null);

        programId = response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID);
        loadProgram.setProgramId(programId);
        context.setAttribute("expectedloadProgram", loadProgram);
        Log.endTestCase("loadPgmEcobee_01_Create");
    }

    @Test(dependsOnMethods = { "loadPgmEcobee_01_Create" })
    public void loadPgmEcobee_02_Get(ITestContext context) throws IOException {

        Log.startTestCase("loadPgmEcobee_02_Get");
        MockLoadProgram expectedLoadProgram = (MockLoadProgram) context.getAttribute("expectedloadProgram");

        ExtractableResponse<?> response = ApiCallHelper.get("getLoadProgram", programId.toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
        context.setAttribute("name", response.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME));
        MockLoadProgram actualLoadProgram = response.as(MockLoadProgram.class);

        assertTrue("Expected and actual name is different", expectedLoadProgram.getName().equals(actualLoadProgram.getName()));
        assertTrue("Expected and actual type is different", expectedLoadProgram.getType().equals(actualLoadProgram.getType()));
        assertTrue("Expected and actual gear name is different",
                   expectedLoadProgram.getGears().get(0).getGearName().equals(actualLoadProgram.getGears().get(0).getGearName()));
        assertTrue("Expected and actual gear control method is different",
                   expectedLoadProgram.getGears().get(0).getControlMethod().equals(actualLoadProgram.getGears().get(0).getControlMethod()));
        Log.endTestCase("loadPgmEcobee_02_Get");
    }

    @Test(dependsOnMethods = { "loadPgmEcobee_01_Create" })
    public void loadPgmEcobee_03_Update(ITestContext context) {

        Log.startTestCase("loadPgmEcobee_03_Update");
        String name = "Auto_LmEcobeeProgram_Update";
        String gearName = "TestGearUpdate";
        MockLoadProgram updateLoadProgram = (MockLoadProgram) context.getAttribute("expectedloadProgram");
        updateLoadProgram.setName(name);
        updateLoadProgram.getGears().get(0).setGearName(gearName);

        ExtractableResponse<?> response = ApiCallHelper.post("updateLoadProgram", updateLoadProgram, programId.toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getLoadProgram", programId.toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
        assertTrue("Name Should be : " + name, name.equals(getupdatedResponse.path("name")));
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME, name);
        Log.endTestCase("loadPgmEcobee_03_Update");
    }

    @Test(dependsOnMethods = { "loadPgmEcobee_01_Create" })
    public void loadPgmEcobee_04_Copy(ITestContext context) {
        Log.startTestCase("loadPgmEcobee_04_Copy");
        MockLoadProgramCopy loadProgramCopy = LoadProgramSetupHelper.buildLoadProgramCopyRequest(MockPaoType.LM_ECOBEE_PROGRAM,
                                                                                                 (Integer) context.getAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID));

        ExtractableResponse<?> response = ApiCallHelper.post("copyLoadProgram", loadProgramCopy, programId.toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);
        assertTrue("Program Id should not be Null", response.path("programId") != null);
        assertTrue("Copy Program Id should be different", !response.path("programId").toString().equals(programId));

        context.setAttribute("copyPgmId", response.path("programId").toString());
        context.setAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME, loadProgramCopy.getName());
        Log.endTestCase("loadPgmEcobee_04_Copy");
    }

    @Test(dependsOnMethods = { "loadPgmEcobee_01_Create" })
    public void loadPgmEcobee_05_Delete(ITestContext context) {
        Log.startTestCase("loadPgmEcobee_05_Delete");
        MockLMDto deleteObject = MockLMDto.builder().name((String) context.getAttribute(LoadProgramSetupHelper.CONTEXT_PROGRAM_NAME)).build();

        // Delete LoadProgram created
        ExtractableResponse<?> response = ApiCallHelper.delete("deleteLoadProgram", deleteObject, programId.toString());
        assertTrue("Status code should be 200", response.statusCode() == 200);

        // Delete LoadGroup created
        MockLoadGroupBase loadGroup = (MockLoadGroupBase) context.getAttribute("loadGroupEcobee");
        deleteObject.setName(loadGroup.getName());
        ExtractableResponse<?> response1 = ApiCallHelper.delete("deleteloadgroup", deleteObject, loadGroup.getId().toString());
        assertTrue("Status code should be 200", response1.statusCode() == 200);

        // Delete Copy LoadProgram
        deleteObject = MockLMDto.builder().name((String) context.getAttribute(LoadProgramSetupHelper.CONTEXT_COPIED_PROGRAM_NAME)).build();
        ExtractableResponse<?> response2 = ApiCallHelper.delete("deleteLoadProgram", deleteObject, context.getAttribute("copyPgmId").toString());
        assertTrue("Status code should be 200", response2.statusCode() == 200);
        Log.endTestCase("loadPgmEcobee_05_Delete");
    }

    @Test(dataProvider = "ProgramNameData")
    public void loadPgmEcobee_06_ProgramNameValidation(String programName, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadPgmEcobee_06_ProgramNameValidation");
        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setName(programName);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> response = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        assertTrue("Expected message should be - Validation error", response.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(response.path("fieldErrors.code[0]")));
        Log.endTestCase("loadPgmEcobee_06_ProgramNameValidation");

    }

    /**
     * DataProvider provides ProgramName data to test method in the form of object array Data provided - col1 : Program
     * Name col2 : Expected field errors code in response col3 : Expected response code
     */
    @DataProvider(name = "ProgramNameData")
    public Object[][] getProgramNameData() {

        return new Object[][] { { "", "Name is required.", 422 },
                { "Test\\@SpecialChar", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "Test,SpecialChar", "Cannot be blank or include any of the following characters: / \\ , ' \" |", 422 },
                { "TestNameMoreThanSixtyCharacter_TestNameMoreThanSixtyCharacters", "Exceeds maximum length of 60.", 422 } };
    }

    @Test(dataProvider = "TriggerOffsetData")
    public void loadPgmEcobee_07_TriggerOffsetValidation(Double triggerOffset, String expectedFieldCode, int expectedStatusCode) {

        Log.startTestCase("loadPgmEcobee_07_TriggerOffsetValidation");

        mockLoadProgram = buildMockLoadProgram();
        mockLoadProgram.setTriggerOffset(triggerOffset);
        mockLoadProgram.setNotification(null);

        ExtractableResponse<?> response = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue("Status code should be " + expectedStatusCode, response.statusCode() == expectedStatusCode);
        assertTrue("Expected message should be - Validation error", response.path("message").equals("Validation error"));
        assertTrue("Expected code in response is not correct", expectedFieldCode.equals(response.path("fieldErrors.code[0]")));
        Log.endTestCase("loadPgmEcobee_07_TriggerOffsetValidation");

    }

    /**
     * DataProvider provides TriggerOffset data to test method in the form of object array Data provided - col1 :
     * Trigger Offset col2 : Expected field errors code in response col3 : Expected response code
     */
    @DataProvider(name = "TriggerOffsetData")
    public Object[][] getTriggerOffsetData() {

        return new Object[][] { { (Double) 100000.0, "Must be between 0 and 100,000.", 422 }, // Max Range
                { (Double) (-1.0), "Must be between 0 and 100,000.", 422 } // Min Range
        };
    }

    @Test
    public void loadPgmEcobee_08_CopyWithInvalidGroupId() {

        Log.startTestCase("loadPgmEcobee_08_CopyWithInvalidGroupId");
        MockLoadProgramCopy loadProgramCopy = LoadProgramSetupHelper.buildLoadProgramCopyRequest(MockPaoType.LM_ECOBEE_PROGRAM, programId);

        ExtractableResponse<?> response = ApiCallHelper.post("copyLoadProgram", loadProgramCopy, "999999");
        assertTrue("Status code should be 400", response.statusCode() == 400);
        assertTrue("Expected message should be - Validation error", response.path("message").equals("A PAObject with id 999999 cannot be found."));

        Log.endTestCase("loadPgmEcobee_08_CopyWithInvalidGroupId");
    }

    /**
     * Test case to validate Load Program cannot be created with null load group id and validates valid error
     * message in response
     */
    @Test
    public void loadPgmEcobee_09_CreateWithLoadGroupIdAsNull(ITestContext context) {

        MockLoadGroupBase loadGroup = LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_EMETCON);
        context.setAttribute(LoadGroupHelper.CONTEXT_MOCK_LOAD_GROUP, loadGroup);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroup);
        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_ID, programConstraint.getId());
        context.setAttribute(ProgramConstraintHelper.CONTEXT_PROGRAM_CONSTRAINT_NAME, programConstraint.getName());
        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.EcobeeCycle);

        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ECOBEE_PROGRAM, loadGroups,
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
     * This is to build Mock LoadProgram payload to be used for negative scenarios test cases
     */
    public MockLoadProgram buildMockLoadProgram() {

        MockLoadGroupBase loadGroupEcobee = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_ECOBEE);
        loadGroupEcobee.setId(3333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupEcobee);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.EcobeeCycle);
        gearTypes.add(MockGearControlMethod.EcobeeSetpoint);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ECOBEE_PROGRAM,
                                                                                     loadGroups,
                                                                                     gearTypes,
                                                                                     programConstraint.getId());

        return loadProgram;
    }
}
