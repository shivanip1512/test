package com.cannontech.rest.api.dr.controlScenario;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.controlArea.request.MockControlArea;
import com.cannontech.rest.api.controlArea.request.MockControlAreaTriggerType;
import com.cannontech.rest.api.controlScenario.request.MockControlScenario;
import com.cannontech.rest.api.controlScenario.request.MockProgramDetails;
import com.cannontech.rest.api.dr.helper.ControlAreaHelper;
import com.cannontech.rest.api.dr.helper.ControlScenarioHelper;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class ControlScenarioApiTest {

    private MockLoadProgram loadProgram = null;
    HashMap<Integer, String> createdControlScenarios = new HashMap<Integer, String>();

    /**
     * Control Scenario creation With Program Assigned To Control Area
     */
    @Test
    public void controlScenario_01_CreateWithProgramAssignedToControlArea(ITestContext context) {
        loadProgram_Create();
        assignProgramToControlArea(context);
        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);
        Integer controlScenarioId = createResponse.path(ControlScenarioHelper.CONTEXT_CONTROL_SCENARIO_ID);
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(controlScenarioId != null, "Control Scenario Id should not be Null");
        context.setAttribute("expectedControlScenario", controlScenario);
        context.setAttribute("controlScenarioId", controlScenarioId);
    }

    /**
     * This test case validates fields of Control Scenario created in Create_01_ControlScenarioWithProgram
     */
    @Test(dependsOnMethods = "controlScenario_01_CreateWithProgramAssignedToControlArea")
    public void controlScenario_02_Get(ITestContext context) {

        Log.startTestCase("controlScenario_02_Get");
        Log.info("Control Scenario Id of Control Scenario is : " + context.getAttribute("controlScenarioId"));

        MockControlScenario controlScenario = (MockControlScenario) context.getAttribute("expectedControlScenario");
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getControlScenario",
                context.getAttribute("controlScenarioId").toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockControlScenario controlScenarioGetResponse = getResponse.as(MockControlScenario.class);
        assertTrue(controlScenario.getName().equals(controlScenarioGetResponse.getName()),
                "Name Should be : " + controlScenarioGetResponse.getName());
        assertTrue(
                controlScenario.getAllPrograms().get(0).getProgramId()
                        .equals(controlScenarioGetResponse.getAllPrograms().get(0).getProgramId()),
                "All Programs Program Id Should be : " + controlScenario.getAllPrograms().get(0).getProgramId());
        assertTrue(
                controlScenario.getAllPrograms().get(0).getStartOffsetInMinutes()
                        .equals(controlScenarioGetResponse.getAllPrograms().get(0).getStartOffsetInMinutes()),
                "Start Offset In Minutes Should be : " + controlScenario.getAllPrograms().get(0).getStartOffsetInMinutes());
        assertTrue(
                controlScenario.getAllPrograms().get(0).getStopOffsetInMinutes()
                        .equals(controlScenarioGetResponse.getAllPrograms().get(0).getStopOffsetInMinutes()),
                "Stop Offset In Minutes Should be : " + controlScenario.getAllPrograms().get(0).getStopOffsetInMinutes());
        assertTrue(
                controlScenario.getAllPrograms().get(0).getGears().get(0)
                        .getId() == (controlScenarioGetResponse.getAllPrograms().get(0).getGears().get(0).getId()),
                "Gear Id Should be : " + controlScenario.getAllPrograms().get(0).getGears().get(0).getId());
        Log.endTestCase("controlScenario_02_Get");
    }

    /**
     * This test case updates name of control Scenario created in controlScenario_01_CreateWithoutProgramAndTrigger
     */
    @Test(dependsOnMethods = "controlScenario_02_Get")
    public void controlScenario_03_Update(ITestContext context) {
        Log.startTestCase("controlScenario_03_Update");

        MockControlScenario controlScenario = (MockControlScenario) context.getAttribute("expectedControlScenario");
        String controlScenarioName = "controlScenarioTest_Name_Update";
        controlScenario.setName(controlScenarioName);
        controlScenario.getAllPrograms().get(0).setStartOffsetInMinutes(1000);
        context.setAttribute("controlScenarioName", controlScenarioName);
        ExtractableResponse<?> updatedResponse = ApiCallHelper.post("updateControlScenario",
                controlScenario,
                context.getAttribute("controlScenarioId").toString());
        assertTrue(updatedResponse.statusCode() == 200, "Status code should be 200");
        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getControlScenario",
                context.getAttribute("controlScenarioId").toString());

        MockControlScenario updatedControlScenarioResponse = getupdatedResponse.as(MockControlScenario.class);
        assertTrue(controlScenarioName.equals(updatedControlScenarioResponse.getName()),
                "Name Should be : " + updatedControlScenarioResponse.getName());
        assertTrue(
                controlScenario.getAllPrograms().get(0).getStartOffsetInMinutes()
                        .equals(updatedControlScenarioResponse.getAllPrograms().get(0).getStartOffsetInMinutes()),
                "Start Offset In Minutes Should be : "
                        + updatedControlScenarioResponse.getAllPrograms().get(0).getStartOffsetInMinutes());
        Log.endTestCase("controlScenario_03_Update");
    }

    /**
     * Negative validation when Control Scenario is created with same name used while creation of Control Scenario in
     * controlScenario_01_CreateWithProgramAssignedToControlArea
     */
    @Test(dependsOnMethods = "controlScenario_03_Update")
    public void controlScenario_04_Name_Is_Same_Validation(ITestContext context) {

        MockControlScenario buildControlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        MockControlScenario controlScenario = (MockControlScenario) context.getAttribute("expectedControlScenario");
        buildControlScenario.setName(controlScenario.getName());
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", buildControlScenario);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name must be unique."),
                "Expected code in response is not correct");
    }

    /**
     * To an existing Control Scenario assigning other Gear to a program
     */
    @Test(dependsOnMethods = "controlScenario_04_Name_Is_Same_Validation")
    public void controlScenario_05_AssigningOtherGear(ITestContext context) {

        MockControlScenario controlScenario = (MockControlScenario) context.getAttribute("expectedControlScenario");

        int noOfGears = loadProgram.getGears().size();
        int gearNumber = 0;

        for (int i = 0; i < noOfGears; i++) {
            if (loadProgram.getGears().get(i).getGearName().equals("TestGear2")) {
                gearNumber = loadProgram.getGears().get(i).getGearNumber();
            }
        }

        MockLMDto gear = MockLMDto.builder().id(gearNumber).build();
        controlScenario.getAllPrograms().get(0).getGears().set(0, gear);

        ExtractableResponse<?> updatedResponse = ApiCallHelper.post("updateControlScenario",
                controlScenario,
                context.getAttribute("controlScenarioId").toString());
        assertTrue(updatedResponse.statusCode() == 200, "Status code should be 200");
        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getControlScenario",
                context.getAttribute("controlScenarioId").toString());

        MockControlScenario updatedControlScenarioResponse = getupdatedResponse.as(MockControlScenario.class);
        assertTrue(updatedControlScenarioResponse.getAllPrograms().get(0).getGears().get(0).getName().equals("TestGear2"),
                "Gear Name should get updated to : TestGear2");
    }

    /**
     * Control Scenario creation With already assigned Program to a Control Scenario
     */
    @Test(dependsOnMethods = "controlScenario_05_AssigningOtherGear")
    public void controlScenario_06_CreateWithAlreadyAssignedProgramToControlScenario(ITestContext context) {

        MockControlScenario controlScenarioWithAssignedProgram = ControlScenarioHelper.buildControlScenario(loadProgram);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenarioWithAssignedProgram);
        Integer controlScenarioId = createResponse.path(ControlScenarioHelper.CONTEXT_CONTROL_SCENARIO_ID);
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(controlScenarioId != null, "Control Scenario Id should not be Null");
        context.setAttribute("expectedControlScenarioWithAssignedProgram", controlScenarioWithAssignedProgram);
        context.setAttribute("controlScenarioIdWithAssignedProgram", controlScenarioId);
        ExtractableResponse<?> getResponse = ApiCallHelper.get("getControlScenario",
                controlScenarioId.toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockControlScenario controlScenarioGetResponse = getResponse.as(MockControlScenario.class);
        createdControlScenarios.put(controlScenarioId, controlScenarioGetResponse.getName().toString());
    }

    /**
     * Unassigns program from a Control Scenario
     */
    @Test(dependsOnMethods = "controlScenario_06_CreateWithAlreadyAssignedProgramToControlScenario")
    public void controlScenario_19_UnassigningProgramFromControlScenario(ITestContext context) {

        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        controlScenario.setAllPrograms(null);
        String controlScenarioName = "ControlScenarioTestUnassign";
        controlScenario.setName(controlScenarioName);
        context.setAttribute("controlScenarioName", controlScenarioName);
        createdControlScenarios.put((Integer) context.getAttribute("controlScenarioId"), controlScenarioName);
        ExtractableResponse<?> updatedResponse = ApiCallHelper.post("updateControlScenario",
                controlScenario,
                context.getAttribute("controlScenarioId").toString());
        assertTrue(updatedResponse.statusCode() == 200, "Status code should be 200");
        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getControlScenario",
                context.getAttribute("controlScenarioId").toString());

        MockControlScenario updatedControlScenarioResponse = getupdatedResponse.as(MockControlScenario.class);
        assertTrue(updatedControlScenarioResponse.getId().equals(context.getAttribute("controlScenarioId")),
                "Control Scenario is updated successfully");
        assertTrue(updatedControlScenarioResponse.getAllPrograms().isEmpty(),
                "Program is successfully unassigned from Control Scenario");
    }

    /**
     * This test case deletes
     * Control Scenario created in controlScenario_01_CreateWithProgramAssignedToControlArea,
     * controlScenario_06_CreateWithAlreadyAssignedProgramToControlScenario
     * Control Area, Load Program and Load Group used for above mentioned Control Scenario's
     */
    @Test(dependsOnMethods = "controlScenario_19_UnassigningProgramFromControlScenario")
    public void controlScenario_07_Delete(ITestContext context) {

        SoftAssert softAssert = new SoftAssert();
        MockLMDto deleteObject = MockLMDto.builder().build();

        // Delete Control Scenario's
        for (Map.Entry<Integer, String> map : createdControlScenarios.entrySet()) {
            deleteObject.setName(map.getValue().toString());
            ExtractableResponse<?> response = ApiCallHelper.delete("deleteControlScenario", deleteObject,
                    map.getKey().toString());
            softAssert.assertTrue(response.statusCode() == 200, "Status code should be 200, delete Control Scenario failed.");
        }

        // Get request to validate Control Scenario's is deleted
        for (Map.Entry<Integer, String> map : createdControlScenarios.entrySet()) {
            ExtractableResponse<?> getDeletedControlScenarioResponse = ApiCallHelper.get("getControlScenario",
                    map.getKey().toString());
            softAssert.assertTrue(getDeletedControlScenarioResponse.statusCode() == 400, "Status code should be 400");
            softAssert.assertTrue(
                    ValidationHelper.validateErrorMessage(getDeletedControlScenarioResponse, "Scenario Id not found"),
                    "Expected error message Should be: Scenario Id not found");
        }

        // Delete Control Area
        deleteObject = MockLMDto.builder().name(context.getAttribute("controlAreaName").toString()).build();
        Log.info("Delete Control Area is : " + deleteObject);
        ExtractableResponse<?> deleteAreaResponse = ApiCallHelper.delete("deleteControlArea",
                deleteObject,
                context.getAttribute("controlAreaId").toString());
        softAssert.assertTrue(deleteAreaResponse.statusCode() == 200, "Status code should be 200, delete Control Area failed.");

        // Delete Load Program
        deleteObject = MockLMDto.builder().name(loadProgram.getName()).build();
        Log.info("Delete Load Program is : " + deleteObject);
        ExtractableResponse<?> deleteProgramResponse = ApiCallHelper.delete("deleteLoadProgram",
                deleteObject,
                loadProgram.getProgramId().toString());
        softAssert.assertTrue(deleteProgramResponse.statusCode() == 200,
                "Status code should be 200, delete Load Program failed.");

        // Delete Load Group
        deleteObject = MockLMDto.builder().name(loadProgram.getAssignedGroups().get(0).getGroupName()).build();
        ExtractableResponse<?> deleteGroupResponse = ApiCallHelper.delete("deleteloadgroup",
                deleteObject,
                loadProgram.getAssignedGroups().get(0).getGroupId().toString());
        softAssert.assertTrue(deleteGroupResponse.statusCode() == 200, "Status code should be 200, delete Load Group failed.");
        softAssert.assertAll();
    }

    /**
     * Negative Validation for Control Scenario creation With Program not Assigned To Control Area
     */
    @Test(dependsOnMethods = "controlScenario_07_Delete")
    public void controlScenario_08_CreateWithProgramNotAssignedToControlArea(ITestContext context) {
        loadProgram_Create();
        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);
        assertTrue(createResponse.statusCode() == 400, "Status code should be " + 400);
        assertTrue(createResponse.asString().contains("Program Id not found"),
                "Program Id not found in Available Programs list or Program Id is not assigned to a Control area.");
    }

    /**
     * Negative Validation for Program Id passed as blank
     */
    @Test(dependsOnMethods = "controlScenario_08_CreateWithProgramNotAssignedToControlArea")
    public void controlScenario_12_ProgramIdPassedAsBlank(ITestContext context) {
        assignProgramToControlArea(context);
        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        controlScenario.getAllPrograms().get(0).setProgramId(null);
        ;
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "allPrograms[0].programId", "Program Id is required."),
                "Expected code in response is not correct");
    }

    /**
     * Negative Validation for Start Offset passed as blank
     */
    @Test(dependsOnMethods = "controlScenario_12_ProgramIdPassedAsBlank")
    public void controlScenario_13_StartOffsetPassedAsBlank(ITestContext context) {
        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        controlScenario.getAllPrograms().get(0).setStartOffsetInMinutes(null);
        ;
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "allPrograms[0].startOffsetInMinutes", "Field is required."),
                "Expected code in response is not correct");
    }

    /**
     * Negative Validation for Start Offset passed as less than min value
     */
    @Test(dependsOnMethods = "controlScenario_12_ProgramIdPassedAsBlank")
    public void controlScenario_14_StartOffsetPassedAsLessThanMinValue(ITestContext context) {
        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        controlScenario.getAllPrograms().get(0).setStartOffsetInMinutes(-1);
        ;
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "allPrograms[0].startOffsetInMinutes",
                        "Must be between 0 and 1,439."),
                "Expected code in response is not correct");
    }

    /**
     * Negative Validation for Start Offset passed as greater than max value
     */
    @Test(dependsOnMethods = "controlScenario_12_ProgramIdPassedAsBlank")
    public void controlScenario_15_StartOffsetPassedAsGreaterThanMaxValue(ITestContext context) {
        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        controlScenario.getAllPrograms().get(0).setStartOffsetInMinutes(-1);
        ;
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "allPrograms[0].startOffsetInMinutes",
                        "Must be between 0 and 1,439."),
                "Expected code in response is not correct");
    }

    /**
     * Negative Validation for Stop Offset passed as blank
     */
    @Test(dependsOnMethods = "controlScenario_12_ProgramIdPassedAsBlank")
    public void controlScenario_16_StopOffsetPassedAsBlank(ITestContext context) {
        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        controlScenario.getAllPrograms().get(0).setStopOffsetInMinutes(null);
        ;
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "allPrograms[0].stopOffsetInMinutes", "Field is required."),
                "Expected code in response is not correct");
    }

    /**
     * Negative Validation for Stop Offset passed as less than min value
     */
    @Test(dependsOnMethods = "controlScenario_12_ProgramIdPassedAsBlank")
    public void controlScenario_17_StopOffsetPassedAsLessThanMinValue(ITestContext context) {
        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        controlScenario.getAllPrograms().get(0).setStopOffsetInMinutes(-1);
        ;
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "allPrograms[0].stopOffsetInMinutes",
                        "Must be between 0 and 1,439."),
                "Expected code in response is not correct");
    }

    /**
     * Negative Validation for Stop Offset passed as greater than max value
     */
    @Test(dependsOnMethods = "controlScenario_12_ProgramIdPassedAsBlank")
    public void controlScenario_18_StopOffsetPassedAsGreaterThanMaxValue(ITestContext context) {
        MockControlScenario controlScenario = ControlScenarioHelper.buildControlScenario(loadProgram);
        controlScenario.getAllPrograms().get(0).setStopOffsetInMinutes(-1);
        ;
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "allPrograms[0].stopOffsetInMinutes",
                        "Must be between 0 and 1,439."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Scenario name field is passed as blank while creation of Control Scenario
     */
    @Test
    public void controlScenario_09_NameAsBlankValidation(ITestContext context) {

        MockControlScenario controlScenario = buildControlScenario("");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Scenario name is passed with special characters | , / while creation of Control Scenario
     */
    @Test
    public void controlScenario_10_NameWithSpecialCharactersValidation(ITestContext context) {

        MockControlScenario controlScenario = buildControlScenario("controlScenarioTest_\"Name");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "name",
                        "Cannot be blank or include any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Scenario name is passed with more than 60 characters while creation of Control Scenario
     */
    @Test
    public void controlScenario_11_NameWithMoreThanSixtyCharactersValidation(ITestContext context) {

        MockControlScenario controlScenario = buildControlScenario(
                "TestControlScenarioName_MoreThanSixtyCharacter_TestControlScenarioNames");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlScenario", controlScenario);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Exceeds maximum length of 60."),
                "Expected code in response is not correct");
    }

    /**
     * This deletes
     * Control Area, Load Program and Load Group used for Negative Validation of Control Scenario's
     */
    @AfterClass
    public void deleteCreatedProgramAndControlAreaInNegativeValidation(ITestContext context) {
        SoftAssert softAssert = new SoftAssert();
        MockLMDto deleteObject = MockLMDto.builder().build();

        // Delete Control Area
        deleteObject = MockLMDto.builder().name(context.getAttribute("controlAreaName").toString()).build();
        Log.info("Delete Control Area is : " + deleteObject);
        ExtractableResponse<?> deleteAreaResponse = ApiCallHelper.delete("deleteControlArea",
                deleteObject,
                context.getAttribute("controlAreaId").toString());
        softAssert.assertTrue(deleteAreaResponse.statusCode() == 200, "Status code should be 200");

        // Delete Load Program
        deleteObject = MockLMDto.builder().name(loadProgram.getName()).build();
        Log.info("Delete Load Program is : " + deleteObject);
        ExtractableResponse<?> deleteProgramResponse = ApiCallHelper.delete("deleteLoadProgram",
                deleteObject,
                loadProgram.getProgramId().toString());
        softAssert.assertTrue(deleteProgramResponse.statusCode() == 200, "Status code should be 200");

        // Delete Load Group
        deleteObject = MockLMDto.builder().name(loadProgram.getAssignedGroups().get(0).getGroupName()).build();
        ExtractableResponse<?> deleteGroupResponse = ApiCallHelper.delete("deleteloadgroup",
                deleteObject,
                loadProgram.getAssignedGroups().get(0).getGroupId().toString());
        softAssert.assertTrue(deleteGroupResponse.statusCode() == 200, "Status code should be 200, delete Load Group failed.");
        softAssert.assertAll();
    }

    public MockControlScenario buildControlScenario(String controlScenarioName) {
        MockLMDto gear = MockLMDto.builder().id(loadProgram.getGears().get(0).getGearNumber()).build();
        List<MockLMDto> gears = new ArrayList<>();
        gears.add(gear);

        MockProgramDetails program = MockProgramDetails.builder()
                .programId(loadProgram.getProgramId())
                .startOffsetInMinutes(600)
                .stopOffsetInMinutes(300)
                .gears(gears)
                .build();

        List<MockProgramDetails> allPrograms = new ArrayList<>();
        allPrograms.add(program);

        MockControlScenario controlScenario = MockControlScenario.builder()
                .name(controlScenarioName)
                .allPrograms(allPrograms)
                .build();

        return controlScenario;

    }

    /**
     * Method to assign created program to a control Scenario
     */
    public void assignProgramToControlArea(ITestContext context) {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                loadProgram.getProgramId());
        ExtractableResponse<?> response = ApiCallHelper.post("saveControlArea", controlArea);
        Integer controlAreaId = response.path(ControlAreaHelper.CONTEXT_CONTROLAREA_ID);
        assertTrue(response.statusCode() == 200, "Status code should be 200");
        assertTrue(controlAreaId != null, "Control Area Id should not be Null");
        context.setAttribute("controlAreaName", controlArea.getName());
        context.setAttribute("controlAreaId", controlAreaId);
    }

    /**
     * Method to create load program as we need to pass load program in request of Control Scenario creation.
     */
    public void loadProgram_Create() {

        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_ECOBEE));

        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.EcobeeCycle);
        gearTypes.add(MockGearControlMethod.EcobeeCycle);

        loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_ECOBEE_PROGRAM, loadGroups, gearTypes,
                programConstraint.getId());
        loadProgram.setNotification(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", loadProgram);
        Integer programId = createResponse.path(LoadProgramSetupHelper.CONTEXT_PROGRAM_ID);
        loadProgram.setProgramId(programId);

        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(programId != null, "Program Id should not be Null");

    }

}
