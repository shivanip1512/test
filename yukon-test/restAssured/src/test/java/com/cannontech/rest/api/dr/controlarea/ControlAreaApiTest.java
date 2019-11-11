package com.cannontech.rest.api.dr.controlarea;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockApiError;
import com.cannontech.rest.api.common.model.MockApiGlobalError;
import com.cannontech.rest.api.common.model.MockLMDto;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.controlArea.request.MockControlArea;
import com.cannontech.rest.api.controlArea.request.MockControlAreaTrigger;
import com.cannontech.rest.api.controlArea.request.MockControlAreaTriggerType;
import com.cannontech.rest.api.controlArea.request.MockDailyDefaultState;
import com.cannontech.rest.api.dr.helper.ControlAreaHelper;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.Log;
import com.cannontech.rest.api.utilities.ValidationHelper;

import io.restassured.response.ExtractableResponse;

public class ControlAreaApiTest {

    private MockLoadProgram loadProgram = null;

    @AfterClass
    public void tearDown(ITestContext context) {

        // Delete Control Area
        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("controlAreaName").toString()).build();
        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteAreaResponse = ApiCallHelper.delete("deleteControlArea",
                lmDeleteObject,
                context.getAttribute("controlAreaId").toString());
        assertTrue(deleteAreaResponse.statusCode() == 200, "Status code should be 200");

        // Delete Load Program
        lmDeleteObject = MockLMDto.builder().name(loadProgram.getName()).build();
        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteProgramResponse = ApiCallHelper.delete("deleteLoadProgram",
                lmDeleteObject,
                loadProgram.getProgramId().toString());
        assertTrue(deleteProgramResponse.statusCode() == 200, "Status code should be 200");

        // Delete Load Group
        lmDeleteObject = MockLMDto.builder().name(loadProgram.getAssignedGroups().get(0).getGroupName()).build();
        ExtractableResponse<?> deleteGroupResponse = ApiCallHelper.delete("deleteloadgroup",
                lmDeleteObject,
                loadProgram.getAssignedGroups().get(0).getGroupId().toString());
        assertTrue(deleteGroupResponse.statusCode() == 200, "Status code should be 200");
    }

    /**
     * This test case validates creation of Control Area with default values
     */
    @Test
    public void controlArea_01_CreateWithoutProgramAndTrigger(ITestContext context) {

        Log.startTestCase("controlArea_01_CreateWithoutProgramAndTrigger");

        MockControlArea controlArea = buildControlArea("controlAreaTest_Name");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        context.setAttribute("controlArea_Id", createResponse.path(ControlAreaHelper.CONTEXT_CONTROLAREA_ID));
        assertTrue(createResponse.statusCode() == 200, "Status code should be 200");
        assertTrue(createResponse.path(ControlAreaHelper.CONTEXT_CONTROLAREA_ID) != null, "Control Area Id should not be Null");
        context.setAttribute("expectedControlArea", controlArea);
        Log.endTestCase("controlArea_01_CreateWithoutProgramAndTrigger");
    }

    /**
     * This test case validates fields of Control Area created in controlArea_01_CreateWithoutProgramAndTrigger
     */
    @Test(dependsOnMethods = "controlArea_01_CreateWithoutProgramAndTrigger")
    public void controlArea_02_Get(ITestContext context) {

        Log.startTestCase("controlArea_02_Get");
        Log.info("Control Area Id of Control Area is : " + context.getAttribute("controlArea_Id"));

        ExtractableResponse<?> getResponse = ApiCallHelper.get("getControlArea",
                context.getAttribute("controlArea_Id").toString());
        assertTrue(getResponse.statusCode() == 200, "Status code should be 200");

        MockControlArea controlAreaGetResponse = getResponse.as(MockControlArea.class);
        context.setAttribute("controlArea_Name", controlAreaGetResponse.getName());

        MockControlArea controlArea = (MockControlArea) context.getAttribute("expectedControlArea");

        assertTrue(controlArea.getName().equals(controlAreaGetResponse.getName()),
                "Name Should be : " + controlAreaGetResponse.getName());
        assertTrue(controlArea.getControlInterval() == controlAreaGetResponse.getControlInterval(),
                "Type Should be : " + controlAreaGetResponse.getControlInterval());
        assertTrue(controlArea.getMinResponseTime().equals(controlAreaGetResponse.getMinResponseTime()),
                "kWCapacity Should be : " + controlAreaGetResponse.getMinResponseTime());
        assertTrue(controlArea.getDailyDefaultState() == (controlAreaGetResponse.getDailyDefaultState()),
                "Group Should be disabled : ");
        assertTrue(controlArea.getAllTriggersActiveFlag() == (controlAreaGetResponse.getAllTriggersActiveFlag()),
                "Control Should be disabled : ");

        Log.endTestCase("controlArea_02_Get");
    }

    /**
     * This test case updates name of control area created in controlArea_01_CreateWithoutProgramAndTrigger
     */
    @Test(dependsOnMethods = "controlArea_02_Get")
    public void controlArea_03_Update(ITestContext context) {
        Log.startTestCase("controlArea_03_Update");

        MockControlArea controlArea = (MockControlArea) context.getAttribute("expectedControlArea");
        String name = "controlAreaTest_Name_Update";
        controlArea.setName(name);
        controlArea.setAllTriggersActiveFlag(true);

        context.setAttribute("controlArea_Name", name);

        ExtractableResponse<?> updatedResponse = ApiCallHelper.post("updateControlArea",
                controlArea,
                context.getAttribute("controlArea_Id").toString());
        assertTrue(updatedResponse.statusCode() == 200, "Status code should be 200");

        ExtractableResponse<?> getupdatedResponse = ApiCallHelper.get("getControlArea",
                context.getAttribute("controlArea_Id").toString());

        MockControlArea updatedControlAreaResponse = getupdatedResponse.as(MockControlArea.class);
        assertTrue(name.equals(updatedControlAreaResponse.getName()), "Name Should be : " + name);
        assertTrue(controlArea.getName().equals(updatedControlAreaResponse.getName()),
                "Type Should be : " + updatedControlAreaResponse.getName());
        assertTrue(controlArea.getAllTriggersActiveFlag().equals(updatedControlAreaResponse.getAllTriggersActiveFlag()),
                "kWCapacity Should be : " + updatedControlAreaResponse.getAllTriggersActiveFlag());
        Log.endTestCase("controlArea_03_Update");
    }

    /**
     * Negative validation when same Control Area name is passed that is used while creation of Control Area created in
     * controlArea_01_CreateWithoutProgramAndTrigger
     */
    @Test(dependsOnMethods = "controlArea_01_CreateWithoutProgramAndTrigger")
    public void controlArea_04_Name_Is_Same_Validation(ITestContext context) {

        Log.startTestCase("controlArea_05_Name_Is_Same_Validation");

        MockControlArea controlArea = buildControlArea("controlAreaTest_Name");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name must be unique."),
                "Expected code in response is not correct");

        Log.endTestCase("controlArea_05_Name_Is_Same_Validation");
    }

    /**
     * This test case deletes Control Area created in controlArea_01_CreateWithoutProgramAndTrigger
     */
    @Test(dependsOnMethods = "controlArea_03_Update")
    public void controlArea_05_Delete(ITestContext context) {

        Log.startTestCase("controlArea_04_Delete");

        MockLMDto lmDeleteObject = MockLMDto.builder().name(context.getAttribute("controlArea_Name").toString()).build();

        Log.info("Delete Load Group is : " + lmDeleteObject);
        ExtractableResponse<?> deleteResponse = ApiCallHelper.delete("deleteControlArea",
                lmDeleteObject,
                context.getAttribute("controlArea_Id").toString());
        assertTrue(deleteResponse.statusCode() == 200, "Status code should be 200");

        // Get request to validate load group is deleted
        ExtractableResponse<?> getDeletedLoadGroupResponse = ApiCallHelper.get("getControlArea",
                context.getAttribute("controlArea_Id").toString());
        assertTrue(getDeletedLoadGroupResponse.statusCode() == 400, "Status code should be 400");
        assertTrue(ValidationHelper.validateErrorMessage(getDeletedLoadGroupResponse, "Control area Id not found"),
                "Expected error message Should contains Text: " + "Control area Id not found");

        Log.endTestCase("controlArea_04_Delete");
    }

    /**
     * Control Area creation With Program and Trigger
     */
    @Test
    public void controlArea_06_CreateWithProgramAndTrigger(ITestContext context) {
        loadProgram_Create();
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
     * Negative validation when Control Area is created with more than 2 triggers
     */
    @Test(dependsOnMethods = "controlArea_06_CreateWithProgramAndTrigger")
    public void controlArea_07_CreateWithMoreThanTwoTriggersValidation() {

        MockControlAreaTrigger trigger1 = ControlAreaHelper.buildTrigger(MockControlAreaTriggerType.THRESHOLD_POINT);
        MockControlAreaTrigger trigger2 = ControlAreaHelper.buildTrigger(MockControlAreaTriggerType.THRESHOLD);
        MockControlAreaTrigger trigger3 = ControlAreaHelper.buildTrigger(MockControlAreaTriggerType.STATUS);
        List<MockControlAreaTrigger> triggers = new ArrayList<>();
        triggers.add(trigger1);
        triggers.add(trigger2);
        triggers.add(trigger3);

        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                loadProgram.getProgramId());
        controlArea.setTriggers(triggers);
        ExtractableResponse<?> response = ApiCallHelper.post("saveControlArea", controlArea);
        MockApiError error = response.as(MockApiError.class);
        List<MockApiGlobalError> globalError = error.getGlobalErrors();
        assertTrue(response.statusCode() == 422, "Status code should be " + 422);
        // assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"), "Expected message should be -
        // Validation
        // error");
        assertTrue("Maximum two valid triggers are allowed.".equals(globalError.get(0).getCode()),
                "Expected code in response is not correct");

    }

    /**
     * Negative validation when Control Area is created with more than 2 triggers
     */
    @Test(dependsOnMethods = "controlArea_06_CreateWithProgramAndTrigger")
    public void controlArea_08_CreateWithAlreadyAssignedProgramIdValidation() {

        MockControlAreaTrigger trigger1 = ControlAreaHelper.buildTrigger(MockControlAreaTriggerType.THRESHOLD_POINT);
        MockControlAreaTrigger trigger2 = ControlAreaHelper.buildTrigger(MockControlAreaTriggerType.THRESHOLD);
        MockControlAreaTrigger trigger3 = ControlAreaHelper.buildTrigger(MockControlAreaTriggerType.STATUS);
        List<MockControlAreaTrigger> triggers = new ArrayList<>();
        triggers.add(trigger1);
        triggers.add(trigger2);
        triggers.add(trigger3);

        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                loadProgram.getProgramId());
        controlArea.setTriggers(triggers);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "programAssignment[0].programId",
                        "Program Id does not exist or already assigned to other Control Area."),
                "Expected code in response is not correct");

    }

    /**
     * Negative validation when Control Area name field is passed as blank while creation of Control Area
     */
    @Test
    public void controlArea_09_NameAsBlankValidation(ITestContext context) {

        Log.startTestCase("controlArea_06_Name_As_Blank_Validation");

        MockControlArea controlArea = buildControlArea("");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Name is required."),
                "Expected code in response is not correct");

        Log.endTestCase("controlArea_06_Name_As_Blank_Validation");
    }

    /**
     * Negative validation when Control Area name is passed with special characters | , / while creation of Control Area
     */
    @Test
    public void controlArea_10_NameWithSpecialCharactersValidation(ITestContext context) {

        Log.startTestCase("controlArea_07_Name_With_Special_Characters_Validation");

        MockControlArea controlArea = buildControlArea("controlAreaTest_\"Name");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "name",
                        "Cannot be blank or include any of the following characters: / \\ , ' \" |"),
                "Expected code in response is not correct");

        Log.endTestCase("loadControl AreaVersacom_07_Control AreaName_With_Special_Characters_Validation");
    }

    /**
     * Negative validation when Control Area name is passed with more than 60 characters while creation of Control Area
     */
    @Test
    public void controlArea_11_NameWithMoreThanSixtyCharactersValidation(ITestContext context) {

        Log.startTestCase("controlArea_08_Name_With_MoreThan_Sixty_Characters_Validation");

        MockControlArea controlArea = buildControlArea("TestControlAreaName_MoreThanSixtyCharacter_TestControlAreaNames");
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);

        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "name", "Exceeds maximum length of 60."),
                "Expected code in response is not correct");

        Log.endTestCase("controlArea_08_Name_With_MoreThan_Sixty_Characters_Validation");
    }

    /**
     * Negative validation when Control Area is created with Threshold value greater than Max Value
     */
    @Test(dependsOnMethods = "controlArea_08_CreateWithAlreadyAssignedProgramIdValidation")
    public void controlArea_12_ThresholdTriggerMaxValueValidation() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD,
                loadProgram.getProgramId());
        controlArea.getTriggers().get(0).setThreshold((double) 1000000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "triggers[0].threshold",
                        "Must be between -1,000,000 and 1,000,000."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Area is created with Threshold value less than Min Value
     */
    @Test(dependsOnMethods = "controlArea_08_CreateWithAlreadyAssignedProgramIdValidation")
    public void controlArea_13_ThresholdTriggerMinValueValidation() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD,
                loadProgram.getProgramId());
        controlArea.getTriggers().get(0).setThreshold((double) -1000000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "triggers[0].threshold",
                        "Must be between -1,000,000 and 1,000,000."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Area is created with Threshold value as blank
     */
    @Test(dependsOnMethods = "controlArea_08_CreateWithAlreadyAssignedProgramIdValidation")
    public void controlArea_14_ThresholdTriggerBlankValueValidation() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD,
                loadProgram.getProgramId());
        controlArea.getTriggers().get(0).setThreshold(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "triggers[0].threshold", "Threshold is required."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Area is created with MinRestoreOffset less than Min Value
     */
    @Test(dependsOnMethods = "controlArea_06_CreateWithProgramAndTrigger")
    public void controlArea_15_ThresholdPointTriggerMinRestoreOffsetMinValueValidation() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                loadProgram.getProgramId());
        controlArea.getTriggers().get(0).setMinRestoreOffset(-100000.0000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "triggers[0].minRestoreOffset",
                        "Must be between -100,000 and 100,000."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Area is created with MinRestoreOffset greater than Max Value
     */
    @Test(dependsOnMethods = "controlArea_06_CreateWithProgramAndTrigger")
    public void controlArea_16_ThresholdPointTriggerMinRestoreOffsetMaxValueValidation() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                loadProgram.getProgramId());
        controlArea.getTriggers().get(0).setMinRestoreOffset(100000.0000);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "triggers[0].minRestoreOffset",
                        "Must be between -100,000 and 100,000."),
                "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Area is created with Program having Start Priority greater than Max Value
     */
    @Test(dependsOnMethods = "controlArea_06_CreateWithProgramAndTrigger")
    public void controlArea_17_StartPriorityMaxValueValidation() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                loadProgram.getProgramId());
        controlArea.getProgramAssignment().get(0).setStartPriority(1025);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "programAssignment[0].startPriority",
                "Must be between 1 and 1,024."), "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Area is created with Program having Start Priority less than Min Value
     */
    @Test(dependsOnMethods = "controlArea_06_CreateWithProgramAndTrigger")
    public void controlArea_18_StartPriorityMinValueValidation() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                loadProgram.getProgramId());
        controlArea.getProgramAssignment().get(0).setStartPriority(0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "programAssignment[0].startPriority",
                "Must be between 1 and 1,024."), "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Area is created with Program having Stop Priority greater than Max Value
     */
    @Test(dependsOnMethods = "controlArea_06_CreateWithProgramAndTrigger")
    public void controlArea_19_StopPriorityMaxValueValidation() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                loadProgram.getProgramId());
        controlArea.getProgramAssignment().get(0).setStopPriority(1025);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "programAssignment[0].stopPriority",
                "Must be between 1 and 1,024."), "Expected code in response is not correct");
    }

    /**
     * Negative validation when Control Area is created with Program having Stop Priority less than Min Value
     */
    @Test(dependsOnMethods = "controlArea_06_CreateWithProgramAndTrigger")
    public void controlArea_20_StopPriorityMinValueValidation() {
        MockControlArea controlArea = ControlAreaHelper.buildControlArea(MockControlAreaTriggerType.THRESHOLD_POINT,
                loadProgram.getProgramId());
        controlArea.getProgramAssignment().get(0).setStopPriority(0);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveControlArea", controlArea);
        assertTrue(createResponse.statusCode() == 422, "Status code should be " + 422);
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be - Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "programAssignment[0].stopPriority",
                "Must be between 1 and 1,024."), "Expected code in response is not correct");
    }

    /**
     * This function build Mock Control Area to be used for creation of Control Area with default values
     */
    public MockControlArea buildControlArea(String controlAreaName) {

        MockControlArea controlArea = MockControlArea.builder()
                .name(controlAreaName)
                .controlInterval(0)
                .minResponseTime(0)
                .dailyDefaultState(MockDailyDefaultState.None)
                .allTriggersActiveFlag(false)
                .build();

        return controlArea;
    }

    /**
     * Method to create load program as we need to pass load program in request of Control Area.
     */
    public void loadProgram_Create() {

        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(LoadGroupHelper.createLoadGroup(MockPaoType.LM_GROUP_ECOBEE));

        MockProgramConstraint programConstraint = ProgramConstraintHelper.createProgramConstraint();

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
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
