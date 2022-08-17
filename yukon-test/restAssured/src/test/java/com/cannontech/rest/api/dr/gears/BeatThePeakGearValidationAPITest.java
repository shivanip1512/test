package com.cannontech.rest.api.dr.gears;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.cannontech.rest.api.common.ApiCallHelper;
import com.cannontech.rest.api.common.model.MockPaoType;
import com.cannontech.rest.api.constraint.request.MockProgramConstraint;
import com.cannontech.rest.api.dr.helper.LoadGroupHelper;
import com.cannontech.rest.api.dr.helper.LoadProgramSetupHelper;
import com.cannontech.rest.api.dr.helper.ProgramConstraintHelper;
import com.cannontech.rest.api.gear.fields.MockGearControlMethod;
import com.cannontech.rest.api.gear.fields.MockBeatThePeakGearFields;
import com.cannontech.rest.api.gear.fields.MockWhenToChange;
import com.cannontech.rest.api.loadProgram.request.MockLoadProgram;
import com.cannontech.rest.api.loadgroup.request.MockLoadGroupBase;
import com.cannontech.rest.api.utilities.ValidationHelper;
import io.restassured.response.ExtractableResponse;

public class BeatThePeakGearValidationAPITest {

    private MockLoadProgram mockLoadProgram = null;

    @BeforeMethod
    public void setup() {
        mockLoadProgram = buildMockLoadProgram();
    }

    /**
     * Test case to validate, Load Program cannot be created with Blank Gear Name
     */
    @Test
    public void gearValidation_01_BlankGearName() {

        mockLoadProgram.getGears().get(0).setGearName("");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", "Gear Name is required."),
                "Expected Error not found: Gear Name is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Gear Name length more than 30 characters
     */
    @Test
    public void gearValidation_02_GearNameGreaterThanThirtyChar() {

        mockLoadProgram.getGears().get(0).setGearName("GearNameLenghthMoreThan30Characters");

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].gearName", "Exceeds maximum length of 30."),
                "Expected Error not found: Exceeds maximum length of 30.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Resend Rate less than min value
     */
    @Test
    public void gearValidation_03_ResendRateLessThanMinValue() {

        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();

        beatThePeakFeilds.setResendInMinutes(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.resendInMinutes",
                "Must be between 0 and 99,999."), "Expected Error not found: Must be between 0 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Resend Rate greater than max value
     */
    @Test
    public void gearValidation_04_ResendRateGreaterThanMaxValue() {

        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();
        beatThePeakFeilds.setResendInMinutes(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.resendInMinutes",
                "Must be between 0 and 99,999."), "Expected Error not found: Must be between 0 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Max Indicator Timeout less than min value
     */
    @Test
    public void gearValidation_05_MaxIndicatorTimeoutLessThanMinValue() {

        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        beatThePeakFeilds.setTimeoutInMinutes(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.timeoutInMinutes",
                        "Must be between 0 and 99,999."),
                "Expected Error not found: Must be between 0 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Max Indicator Timeout greater than max value
     */
    @Test
    public void gearValidation_06_MaxIndicatorTimeoutGreaterThanMaxValue() {

        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();

        beatThePeakFeilds.setTimeoutInMinutes(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.timeoutInMinutes",
                        "Must be between 0 and 99,999."),
                "Expected Error not found: Must be between 0 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with With LED indicator as blank
     */
    @Test
    public void gearValidation_07_LEDIndicatorAsBlank() {
        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();

        beatThePeakFeilds.setIndicator(null);
        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.indicator",
                        "BTP LED Indicator is required."),
                "Expected Error not found: BTP LED Indicator is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Field is Blank
     */
    @Test
    public void gearValidation_08_WhenToChangeFieldAsBlank() {

        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();

        beatThePeakFeilds.setWhenToChangeFields(null);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields",
                "When To Change Fields is required."),
                "Expected Error not found: When To Change Fields is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Duration In Minutes less than min value
     */
    @Test
    public void gearValidation_09_ChangeDurationInMinutesLessThanMinValue() {
        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();

        beatThePeakFeilds.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Duration);
        beatThePeakFeilds.getWhenToChangeFields().setChangeDurationInMinutes(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                "gears[0].fields.whenToChangeFields.changeDurationInMinutes", "Must be between 0 and 99,999."),
                "Expected Error not found: Must be between 0 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Duration In Minutes greater than max value
     */
    @Test
    public void gearValidation_10_ChangeDurationInMinutesGreaterThanMaxValue() {

        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();

        beatThePeakFeilds.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Duration);
        beatThePeakFeilds.getWhenToChangeFields().setChangeDurationInMinutes(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse,
                "gears[0].fields.whenToChangeFields.changeDurationInMinutes", "Must be between 0 and 99,999."),
                "Expected Error not found: Must be between 0 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Number and Offset as blank
     */
    @Test
    public void gearValidation_11_TriggerNumberAndOffsetAsBlank() {

        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();

        beatThePeakFeilds.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerNumber",
                "Trigger Number is required."), "Expected Error not found: Trigger Number is required.");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerOffset",
                "Trigger Offset is required."), "Expected Error not found: Trigger Offset is required.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Number less than min value
     */
    @Test
    public void gearValidation_12_TriggerNumberLessThanMinValue() {
        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();
        beatThePeakFeilds.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        beatThePeakFeilds.getWhenToChangeFields().setTriggerNumber(0);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerNumber",
                "Must be between 1 and 99,999."), "Expected Error not found: Must be between 1 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Number greater than max value
     */
    @Test
    public void gearValidation_13_TriggerNumberGreaterThanMaxValue() {
        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();
        beatThePeakFeilds.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        beatThePeakFeilds.getWhenToChangeFields().setTriggerNumber(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerNumber",
                "Must be between 1 and 99,999."), "Expected Error not found: Must be between 1 and 99,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Offset less than min value
     */
    @Test
    public void gearValidation_14_TriggerOffsetLessThanMinValue() {
        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();
        beatThePeakFeilds.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        beatThePeakFeilds.getWhenToChangeFields().setTriggerOffset(-100005.000000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerOffset",
                        "Must be between -100,000 and 100,000."),
                "Expected Error not found: Must be between -100,000 and 100,000.");
    }

    /**
     * Test case to validate, Load Program cannot be created with Trigger Offset greater than max value
     */
    @Test
    public void gearValidation_15_TriggerOffsetGreaterThanMaxValue() {
        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();
        beatThePeakFeilds.getWhenToChangeFields().setWhenToChange(MockWhenToChange.TriggerOffset);
        beatThePeakFeilds.getWhenToChangeFields().setTriggerOffset(100005.000000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(
                ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.triggerOffset",
                        "Must be between -100,000 and 100,000."),
                "Expected Error not found: Must be between -100,000 and 100,000.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Priority less than min value
     */
    @Test
    public void gearValidation_16_WhenToChangePriorityLessThanMinValue() {
        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears()
                .get(0).getFields();

        beatThePeakFeilds.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Priority);
        beatThePeakFeilds.getWhenToChangeFields().setChangePriority(-1);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.changePriority",
                "Must be between 0 and 9,999."), "Expected Error not found: Must be between 0 and 9,999.");
    }

    /**
     * Test case to validate, Load Program cannot be created with When To Change Priority greater than max value
     */
    @Test
    public void gearValidation_17_WhenToChangePriorityGreaterThanMaxValue() {
        MockBeatThePeakGearFields beatThePeakFeilds = (MockBeatThePeakGearFields) mockLoadProgram.getGears().get(0).getFields();
        beatThePeakFeilds.getWhenToChangeFields().setWhenToChange(MockWhenToChange.Priority);
        beatThePeakFeilds.getWhenToChangeFields().setChangePriority(100000);

        ExtractableResponse<?> createResponse = ApiCallHelper.post("saveLoadProgram", mockLoadProgram);
        assertTrue(createResponse.statusCode() == 422, "Status code should be 422");
        assertTrue(ValidationHelper.validateErrorMessage(createResponse, "Validation error"),
                "Expected message should be: Validation error");
        assertTrue(ValidationHelper.validateFieldError(createResponse, "gears[0].fields.whenToChangeFields.changePriority",
                "Must be between 0 and 9,999."), "Expected Error not found: Must be between 0 and 9,999.");
    }

    /**
     * This is to build Mock LoadProgram payload to be used for negative scenarios test cases
     */
    public MockLoadProgram buildMockLoadProgram() {

        MockLoadGroupBase loadGroupDigiSep = LoadGroupHelper.buildLoadGroup(MockPaoType.LM_GROUP_DIGI_SEP);
        loadGroupDigiSep.setId(333);
        List<MockLoadGroupBase> loadGroups = new ArrayList<>();
        loadGroups.add(loadGroupDigiSep);

        MockProgramConstraint programConstraint = ProgramConstraintHelper.buildProgramConstraint();
        programConstraint.setId(0);
        programConstraint.setName("Default Constraint");

        List<MockGearControlMethod> gearTypes = new ArrayList<>();
        gearTypes.add(MockGearControlMethod.BeatThePeak);
        MockLoadProgram loadProgram = LoadProgramSetupHelper.buildLoadProgramRequest(MockPaoType.LM_DIRECT_PROGRAM,
                loadGroups,
                gearTypes,
                programConstraint.getId());

        return loadProgram;
    }

}
